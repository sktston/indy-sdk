package com.evernym.sdk.vcx.connection;

import com.evernym.sdk.vcx.LibVcx;
import com.evernym.sdk.vcx.ParamGuard;
import com.evernym.sdk.vcx.VcxException;
import com.evernym.sdk.vcx.VcxJava;
import com.sun.jna.Callback;
import com.sun.jna.Pointer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * <h1>VCX Connection API.</h1>
 * VCX Connection APIs<br>
 * Created by abdussami on 05/06/18.<br>
 * Javadoc as written by JJ (Referring to libvcx and python wrapper documents)
 *
 * @version 1.1
 * @since   11/08/2020
 */
public class ConnectionApi extends VcxJava.API {

	private static final Logger logger = LoggerFactory.getLogger("ConnectionApi");

	private static Callback vcxConnectionCreateCB = new Callback() {
		// TODO: This callback and jna definition needs to be fixed for this API
		// it should accept connection handle as well
		@SuppressWarnings({"unused", "unchecked"})
		public void callback(int commandHandle, int err, int connectionHandle) {
			logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], connectionHandle = [" + connectionHandle + "]");
			CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
			if (! checkCallback(future, err)) return;
			Integer result = connectionHandle;
			future.complete(result);
		}
	};

	/**
	 * 1. Create a Connection object that provides a pairwise connection for an institution's user <br>
	 * 2. Create a connection object, represents a single endpoint and can be used for sending and receiving credentials and proofs.<br>
	 *
	 *
	 * @param sourceId Institution's unique ID for the connection
	 * @return completable future
	 * @throws VcxException thrown if an error occurs when calling the underlying SDK
	 * <pre><span style="color: gray;font-style: italic;"> Example:
	 *
	 *   // Create a connection to alice and return the invite details
	 *         int connectionHandle = ConnectionApi.{@link #vcxConnectionCreate vcxConnectionCreate}("alice").get();
	 *   // Connection Connect & invite detail
	 *         ConnectionApi.{@link #vcxConnectionConnect vcxConnectionConnect}(connectionHandle, "{}").get();
	 *         String details = ConnectionApi.{@link @connectionInviteDetails connectionInviteDetails}(connectionHandle, 0).get();
	 *   // addRecordWallet : invitation
	 *         WalletApi.{@link com.evernym.sdk.vcx.wallet.WalletApi#addRecordWallet addRecordWallet}("invitation", "defaultInvitation", details, "").get();
	 *   // Connection Serialize
	 *         String serializedConnection = ConnectionApi.{@link #connectionSerialize connectionSerialize}(connectionHandle).get();
	 *   // Get Pairwise DID
	 *         String pwDid = ConnectionApi.{@link #connectionGetPwDid connectionGetPwDid}(connectionHandle).get();
	 *   // addRecordWallet : connection
	 *         WalletApi.{@link com.evernym.sdk.vcx.wallet.WalletApi#addRecordWallet addRecordWallet}("connection", pwDid, serializedConnection, "").get();
	 *   // Connection Release
	 *         ConnectionApi.{@link #connectionRelease connectionRelease}(connectionHandle); </span></pre>
	 * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/master/src/main/java/webhook/faber/GlobalService.java" target="_blank">VCX JAVA Demo - Connection with Invite Example</a>
	 *
	 */
	public static CompletableFuture<Integer> vcxConnectionCreate(String sourceId) throws VcxException {
		ParamGuard.notNullOrWhiteSpace(sourceId, "sourceId");
		logger.debug("vcxConnectionCreate() called with: sourceId = [ {} ]", sourceId);
		CompletableFuture<Integer> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_connection_create(
				commandHandle,
				sourceId,
				vcxConnectionCreateCB
		);
		checkResult(result);
		return future;
	}

	private static Callback vcxUpdateStateCB = new Callback() {
		@SuppressWarnings({"unused", "unchecked"})
		public void callback(int commandHandle, int err, int s) {
			logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], s = [" + s + "]");
			CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
			if (! checkCallback(future, err)) return;
			Integer result = s;
			future.complete(result);
		}
	};

	/**
	 * Query the agency for the received messages.<br>
	 * Checks for any messages changing state in the connection object and updates the state attribute.<br>
	 *
	 * @param connectionHandle the connection handle
	 * @return Current state of the connection. Possible states:
	 *<pre><span style="color: gray;font-style: italic;">    1 - Initialized
	 *    2 - Request Sent
	 *    3 - Offer Received
	 *    4 - Accepted </span></pre>
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 *   <pre><span style="color: gray;font-style: italic;"> Example:
	 *
	 *   //connection request - At Inviter: after receiving invitation from Invitee
	 *       if (innerType.equals("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/request")) {
	 *           int connectionState = ConnectionApi.{@link #vcxConnectionUpdateState vcxConnectionUpdateState}(connectionHandle).get();
	 *           if (connectionState == VcxState.RequestReceived.getValue()) {
	 *   // new relationship - Get PW DID & Serialized & Record Wallet
	 *               String newPwDid = ConnectionApi.{@link #connectionGetPwDid connectionGetPwDid}(connectionHandle).get();
	 *   // Connection Serialize
	 *               serializedConnection = ConnectionApi.{@link #connectionSerialize connectionSerialize}(connectionHandle).get();
	 *   // Add Record Wallet
	 *               WalletApi.{@link com.evernym.sdk.vcx.wallet.WalletApi#addRecordWallet addRecordWallet}("connection", newPwDid, serializedConnection, "").get();
	 *           }
	 *   </span></pre>
	 * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/master/src/main/java/webhook/faber/GlobalController.java" target="_blank">VCX JAVA Demo - Connection Update State Example</a>
	 */
	public static CompletableFuture<Integer> vcxConnectionUpdateState(int connectionHandle) throws VcxException {
		logger.debug("vcxConnectionUpdateState() called with: connectionHandle = [" + connectionHandle + "]");
		CompletableFuture<Integer> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_connection_update_state(
				commandHandle,
				connectionHandle,
				vcxUpdateStateCB
		);
		checkResult(result);
		return future;
	}

	/**
	 * Update the state of the connection based on the given message.<br>
	 *
	 * @param connectionHandle the connection handle
	 * @param message          the message
	 * @return Current state of the connection. Possible states:
	 * <pre><span style="color: gray;font-style: italic;">    1 - Initialized
	 *    2 - Request Sent
	 *    3 - Offer Received
	 *    4 - Accepted </span></pre>
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 */
	public static CompletableFuture<Integer> vcxConnectionUpdateStateWithMessage(int connectionHandle, String message) throws VcxException {
		logger.debug("vcxConnectionUpdateState() called with: connectionHandle = [" + connectionHandle + "]");
		CompletableFuture<Integer> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_connection_update_state_with_message(
				commandHandle,
				connectionHandle,
				message,
				vcxUpdateStateCB
		);
		checkResult(result);
		return future;
	}

	private static Callback vcxCreateConnectionWithInviteCB = new Callback() {
		@SuppressWarnings({"unused", "unchecked"})
		public void callback(int commandHandle, int err, int connectionHandle) {
			logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], connectionHandle = [" + connectionHandle + "]");
			CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
			if (! checkCallback(future, err)) return;
			// TODO complete with exception if we find error
//            if (err != 0) {
//                future.completeExceptionally();
//            } else {
//
//            }
			Integer result = connectionHandle;
			future.complete(result);
		}
	};

	/**
	 * 1. Create a connection object with a provided invite, represents a single endpoint and can be used for sending and receiving credentials and proofs.<br>
	 * 2. Invite details are provided by the entity offering a connection and generally pulled from a provided QRCode.
	 *
	 * @param invitationId  Institution's unique ID for the connection
	 * @param inviteDetails A string representing a json object which is provided by an entity that wishes to make a connection.
	 * <pre><span style="color: gray;font-style: italic;"> Invite format depends on communication method:
	 *    proprietary:
	 *        {"targetName": "", "statusMsg": "message created", "connReqId": "mugIkrWeMr", "statusCode": "MS-101", "threadId": null, "senderAgencyDetail": {"endpoint": "http://localhost:8080", "verKey": "key", "DID": "did"}, "senderDetail": {"agentKeyDlgProof": {"agentDID": "8f6gqnT13GGMNPWDa2TRQ7", "agentDelegatedKey": "5B3pGBYjDeZYSNk9CXvgoeAAACe2BeujaAkipEC7Yyd1", "signature": "TgGSvZ6+/SynT3VxAZDOMWNbHpdsSl8zlOfPlcfm87CjPTmC/7Cyteep7U3m9Gw6ilu8SOOW59YR1rft+D8ZDg=="}, "publicDID": "7YLxxEfHRiZkCMVNii1RCy", "name": "Faber", "logoUrl": "http://robohash.org/234", "verKey": "CoYZMV6GrWqoG9ybfH3npwH3FnWPcHmpWYUF8n172FUx", "DID": "Ney2FxHT4rdEyy6EDCCtxZ"}}
	 *    aries: https://github.com/hyperledger/aries-rfcs/tree/master/features/0160-connection-protocol#0-invitation-to-connect
	 *        {
	 *        "@type": "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/invitation",
	 *        "label": "Alice",
	 *        "recipientKeys": ["8HH5gYEeNc3z7PYXmd54d4x6qAfCNrqQqEB3nS7Zfu7K"],
	 *        "serviceEndpoint": "https://example.com/endpoint",
	 *        "routingKeys": ["8HH5gYEeNc3z7PYXmd54d4x6qAfCNrqQqEB3nS7Zfu7K"]
	 *        } </span></pre>
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/master/src/main/java/webhook/alice/GlobalService.java" target="_blank">VCX JAVA Demo - Connection with Invite Example</a>
	 */
	public static CompletableFuture<Integer> vcxCreateConnectionWithInvite(String invitationId, String inviteDetails) throws VcxException {
		ParamGuard.notNullOrWhiteSpace(invitationId, "invitationId");
		ParamGuard.notNullOrWhiteSpace(inviteDetails, "inviteDetails");
		logger.debug("vcxCreateConnectionWithInvite() called with: invitationId = [" + invitationId + "], inviteDetails = [****]");
		CompletableFuture<Integer> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_connection_create_with_invite(
				commandHandle,
				invitationId,
				inviteDetails,
				vcxCreateConnectionWithInviteCB
		);
		checkResult(result);
		return future;
	}

	private static Callback vcxConnectionConnectCB = new Callback() {
		@SuppressWarnings({"unused", "unchecked"})
		public void callback(int commandHandle, int err, String inviteDetails) {
			logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], inviteDetails = [****]");
			CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
			if (! checkCallback(future, err)) return;
			// TODO complete with exception if we find error
//            if (err != 0) {
//                future.completeExceptionally();
//            } else {
//
//            }
			String result = inviteDetails;
			future.complete(result);
		}
	};
	/**
	 * Deprecated method
	 *
	 * @param connectionHandle the connection handle
	 * @param connectionType   the connection type
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 */
	@Deprecated
	public static CompletableFuture<String> vcxAcceptInvitation(int connectionHandle, String connectionType) throws VcxException {
		ParamGuard.notNull(connectionHandle, "connectionHandle");
		ParamGuard.notNullOrWhiteSpace(connectionType, "connectionType");
		return vcxConnectionConnect(connectionHandle, connectionType);
	}

	/**
	 * Connect securely and privately to the endpoint represented by the object.
	 *
	 * @param connectionHandle the connection handle
	 * @param connectionType   the connection type
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 * @see "Refer to vcxConnectionCreate example for vcxConnectionConnect demo"
	 * @see #vcxConnectionCreate
	 */
	public static CompletableFuture<String> vcxConnectionConnect(int connectionHandle, String connectionType) throws VcxException {
		ParamGuard.notNull(connectionHandle, "connectionHandle");
		ParamGuard.notNullOrWhiteSpace(connectionType, "connectionType");
		logger.debug("vcxAcceptInvitation() called with: connectionHandle = [" + connectionHandle + "], connectionType = [" + connectionType + "]");
		CompletableFuture<String> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_connection_connect(
				commandHandle,
				connectionHandle,
				connectionType,
				vcxConnectionConnectCB
		);
		checkResult(result);
		return future;
	}

	private static Callback vcxConnectionRedirectCB = new Callback() {
		@SuppressWarnings({"unused", "unchecked"})
		public void callback(int commandHandle, int err) {
			logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "]");
			CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
			if (!checkCallback(future, err)) return;
			future.complete(0);
		}
	};

	/**
	 * Connect securely and privately to the endpoint represented by the object.
	 *
	 * @param connectionHandle         the connection handle
	 * @param redirectConnectionHandle the redirect connection handle
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 */
	public static CompletableFuture<Integer> vcxConnectionRedirect(int connectionHandle, int redirectConnectionHandle) throws VcxException {
		ParamGuard.notNull(connectionHandle, "connectionHandle");
		ParamGuard.notNull(redirectConnectionHandle, "redirectConnectionHandle");
		logger.debug("vcxConnectionRedirect() called with: connectionHandle = [" + connectionHandle + "], redirectConnectionHandle = [" + redirectConnectionHandle + "]");
		CompletableFuture<Integer> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_connection_redirect(
				commandHandle,
				connectionHandle,
				redirectConnectionHandle,
				vcxConnectionRedirectCB
		);
		checkResult(result);
		return future;
	}

	private static Callback vcxConnectionGetRedirectDetailsCB = new Callback() {
		@SuppressWarnings({"unused", "unchecked"})
		public void callback(int commandHandle, int err, String redirectDetails) {
			logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], redirectDetails = [****]");
			CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
			if (!checkCallback(future, err)) return;
			String result = redirectDetails;
			future.complete(result);
		}
	};

	/**
	 * Vcx connection get redirect details completable future.
	 *
	 * @param connectionHandle the connection handle
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 */
	public static CompletableFuture<String> vcxConnectionGetRedirectDetails(int connectionHandle) throws VcxException {
		ParamGuard.notNull(connectionHandle, "connectionHandle");
		logger.debug("vcxConnectionGetRedirectDetails() called with: connectionHandle = [" + connectionHandle + "]");
		CompletableFuture<String> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_connection_get_redirect_details(
				commandHandle,
				connectionHandle,
				vcxConnectionGetRedirectDetailsCB
		);
		checkResult(result);
		return future;
	}


	private static Callback vcxConnectionSerializeCB = new Callback() {
		@SuppressWarnings({"unused", "unchecked"})
		public void callback(int commandHandle, int err, String serializedData) {
			logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], serializedData = [" + serializedData + "]");
			CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
			if (! checkCallback(future, err)) return;
			// TODO complete with exception if we find error
//            if (err != 0) {
//                future.completeExceptionally();
//            } else {
//
//            }
			future.complete(serializedData);
		}
	};

	/**
	 * Takes the Connection object and returns a json string of all its attributes
	 *
	 * @param connectionHandle the connection handle
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 * @see "Refer to vcxConnectionCreate example for connectionSerialize demo"
	 * @see #vcxConnectionCreate
	 */
	public static CompletableFuture<String> connectionSerialize(int connectionHandle) throws VcxException {
		ParamGuard.notNull(connectionHandle, "connectionHandle");
		logger.debug("connectionSerialize() called with: connectionHandle = [" + connectionHandle + "]");
		CompletableFuture<String> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_connection_serialize(
				commandHandle,
				connectionHandle,
				vcxConnectionSerializeCB
		);
		checkResult(result);
		return future;
	}

	private static Callback vcxConnectionDeserializeCB = new Callback() {
		@SuppressWarnings({"unused", "unchecked"})
		public void callback(int commandHandle, int err, int connectionHandle) {
			logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], connectionHandle = [" + connectionHandle + "]");
			CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
			if (! checkCallback(future, err)) return;
			// TODO complete with exception if we find error
//            if (err != 0) {
//                future.completeExceptionally();
//            } else {
//
//            }
			future.complete(connectionHandle);
		}
	};

	/**
	 * Takes a json string representing a connection object and recreates an object matching the json.
	 *
	 * @param connectionData json string representing a connection object. Is an output of `serialize` function.
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 * <pre><span style="color: gray;font-style: italic;"> Example:
	 *
	 *   // Get the message from "mediator agency" using notification information
	 *         String messages = UtilsApi.{@link com.evernym.sdk.vcx.utils.UtilsApi#vcxGetMessages vcxGetMessages}(body.getMsgStatusCode(), body.getMsgUid(), body.getPwDid()).get();
	 *         String message = JsonPath.parse((LinkedHashMap)JsonPath.read(messages,"$.[0].msgs[0]")).jsonString();
	 *         String decryptedPayload = JsonPath.read(message, "$.decryptedPayload");
	 *         String payloadMessage = JsonPath.read(decryptedPayload,"$.@msg");
	 *         String type = JsonPath.read(decryptedPayload,"$.@type.name");
	 *
	 *   // pwDid is used as a connectionId
	 *         String pwDid = JsonPath.read(messages,"$.[0].pairwiseDID");
	 *         String connectionRecord = WalletApi.{@link com.evernym.sdk.vcx.wallet.WalletApi#getRecordWallet getRecordWallet}("connection", pwDid, "").get();
	 *         String serializedConnection = JsonPath.read(connectionRecord,"$.value");
	 *   // Deserialize Connection
	 *         int connectionHandle = ConnectionApi.{@link #connectionDeserialize connectionDeserialize}(serializedConnection).get();
	 * </span></pre>
	 * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/master/src/main/java/webhook/faber/GlobalController.java" target="_blank">VCX JAVA Demo - connectionDeserialize Example</a>
	 */
	public static CompletableFuture<Integer> connectionDeserialize(String connectionData) throws VcxException {
		ParamGuard.notNull(connectionData, "connectionData");
		logger.debug("connectionDeserialize() called with: connectionData = [****]");
		CompletableFuture<Integer> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_connection_deserialize(
				commandHandle,
				connectionData,
				vcxConnectionDeserializeCB
		);
		checkResult(result);
		return future;
	}


	private static Callback vcxConnectionDeleteCB = new Callback() {
		@SuppressWarnings({"unused", "unchecked"})
		public void callback(int commandHandle, int err) {
			logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "]");
			CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
			if (! checkCallback(future, err)) return;
			future.complete(0);
		}
	};

	/**
	 * Delete connection completable future.
	 *
	 * @param connectionHandle the connection handle
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 */
	public static CompletableFuture<Integer> deleteConnection(int connectionHandle) throws VcxException {
		logger.debug("deleteConnection() called with: connectionHandle = [" + connectionHandle + "]");
		CompletableFuture<Integer> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_connection_delete_connection(commandHandle, connectionHandle, vcxConnectionDeleteCB);
		checkResult(result);
		return future;
	}

	private static Callback vcxConnectionInviteDetailsCB = new Callback() {
		@SuppressWarnings({"unused", "unchecked"})
		public void callback(int commandHandle, int err, String details) {
			logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], details = [****]");
			CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
			if (!checkCallback(future, err)) return;
			future.complete(details);
		}
	};

	/**
	 * Connection invite details completable future.
	 *
	 * @param connectionHandle the connection handle
	 * @param abbreviated      the abbreviated
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 * @see "Refer to vcxConnectionCreate example for vcxConnectionConnect demo"
	 * @see #vcxConnectionCreate
	 */
	public static CompletableFuture<String> connectionInviteDetails(int connectionHandle, int abbreviated) throws VcxException {
		logger.debug("connectionInviteDetails() called with: connectionHandle = [" + connectionHandle + "], abbreviated = [****]");
		CompletableFuture<String> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);
		int result = LibVcx.api.vcx_connection_invite_details(commandHandle, connectionHandle, abbreviated, vcxConnectionInviteDetailsCB);
		checkResult(result);
		return future;
	}


	/**
	 * Connection release int.
	 *
	 * @param handle the handle
	 * @return Success
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 * @see "Refer to vcxConnectionCreate example for vcxConnectionConnect demo"
	 * @see #vcxConnectionCreate
	 */
	public static int connectionRelease(int handle) throws VcxException {
		logger.debug("connectionRelease() called with: handle = [" + handle + "]");
		ParamGuard.notNull(handle, "handle");
		int result = LibVcx.api.vcx_connection_release(handle);
		checkResult(result);

		return result;
	}

	private static Callback vcxConnectionGetStateCB = new Callback() {
		@SuppressWarnings({"unused", "unchecked"})
		public void callback(int commandHandle, int err, int state) {
			logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], state = [" + state + "]");
			CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
			if (! checkCallback(future, err)) return;
			future.complete(state);
		}
	};

	/**
	 * Connection get state completable future.
	 *
	 * @param connnectionHandle the connnection handle
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 */
	public static CompletableFuture<Integer> connectionGetState(int connnectionHandle) throws VcxException {
		logger.debug("connectionGetState() called with: connnectionHandle = [" + connnectionHandle + "]");
		CompletableFuture<Integer> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);
		int result = LibVcx.api.vcx_connection_get_state(commandHandle, connnectionHandle, vcxConnectionGetStateCB);
		checkResult(result);
		return future;
	}

	private static Callback voidCb = new Callback() {
		@SuppressWarnings({"unused", "unchecked"})
		public void callback(int commandHandle, int err) {
			logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "]");
			CompletableFuture<Void> future = (CompletableFuture<Void>) removeFuture(commandHandle);
			if (! checkCallback(future, err)) return;
			Void result = null;
			future.complete(result);
		}
	};

	/**
	 * Connection send ping completable future.
	 *
	 * @param connectionHandle the connection handle
	 * @param comment          the comment
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 */
	public static CompletableFuture<Void> connectionSendPing(
			int connectionHandle,
			String comment
	) throws VcxException {
		logger.debug("sendPing() called with: connectionHandle = [" + connectionHandle + "], comment = [" + comment + "]");
		CompletableFuture<Void> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_connection_send_ping(commandHandle, connectionHandle, comment, voidCb);
		checkResult(result);

		return future;
	}

	/**
	 * Connection send discovery features completable future.
	 *
	 * @param connectionHandle the connection handle
	 * @param query            the query
	 * @param comment          the comment
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 */
	public static CompletableFuture<Void> connectionSendDiscoveryFeatures(
			int connectionHandle,
			String query,
			String comment
	) throws VcxException {
		logger.debug("connectionSendDiscoveryFeatures() called with: connectionHandle = [" + connectionHandle + "], query = [" + query + "], comment = [" + comment + "]");
		CompletableFuture<Void> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_connection_send_discovery_features(commandHandle, connectionHandle, query, comment, voidCb);
		checkResult(result);

		return future;
	}

    private static Callback vcxConnectionSendMessageCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, String msgId) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], msgId = [" + msgId + "]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            future.complete(msgId);
        }
    };

	/**
	 * Connection send message completable future.
	 *
	 * @param connectionHandle   the connection handle
	 * @param message            the message
	 * @param sendMessageOptions the send message options
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 */
	public static CompletableFuture<String> connectionSendMessage(int connectionHandle, String message, String sendMessageOptions) throws VcxException {
        logger.debug("connectionSendMessage() called with: connectionHandle = [" + connectionHandle + "], message = [****], sendMessageOptions = [" + sendMessageOptions + "]");
        CompletableFuture<String> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);
        int result = LibVcx.api.vcx_connection_send_message(commandHandle, connectionHandle, message, sendMessageOptions, vcxConnectionSendMessageCB);
        checkResult(result);
        return future;
    }


    private static Callback vcxConnectionSignDataCB = new Callback() {

        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int xcommand_handle, int err, Pointer signature_raw, int signature_len) {

            CompletableFuture<byte[]> future = (CompletableFuture<byte[]>) removeFuture(xcommand_handle);
            if (! checkResult(future, err)) return;

            byte[] encryptedMsg = new byte[signature_len];
            signature_raw.read(0, encryptedMsg, 0, signature_len);

            future.complete(encryptedMsg);
        }
    };


	/**
	 * Connection sign data completable future.
	 *
	 * @param connectionHandle the connection handle
	 * @param data             the data
	 * @param dataLength       the data length
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 */
	public static CompletableFuture<byte[]> connectionSignData(int connectionHandle, byte[] data, int dataLength) throws VcxException {

        ParamGuard.notNull(data, "data");

        CompletableFuture<byte[]> future = new CompletableFuture<byte[]>();
        int commandHandle = addFuture(future);
        int result = LibVcx.api.vcx_connection_sign_data(commandHandle, connectionHandle, data, dataLength, vcxConnectionSignDataCB);
        checkResult(future, result);

        return future;
    }

    private static Callback vcxConnectionVerifySignatureCB = new Callback() {

        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int xcommand_handle, int err, boolean valid) {

            CompletableFuture<Boolean> future = (CompletableFuture<Boolean>) removeFuture(xcommand_handle);
            if (! checkResult(future, err)) return;

            future.complete(valid);
        }
    };


	/**
	 * Connection verify signature completable future.
	 *
	 * @param connectionHandle the connection handle
	 * @param data             the data
	 * @param dataLength       the data length
	 * @param signature        the signature
	 * @param signatureLength  the signature length
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 */
	public static CompletableFuture<Boolean> connectionVerifySignature(int connectionHandle, byte[] data, int dataLength, byte[] signature, int signatureLength) throws VcxException {

        ParamGuard.notNull(data, "data");
        ParamGuard.notNull(signature, "signature");

        CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
        int commandHandle = addFuture(future);
        int result = LibVcx.api.vcx_connection_verify_signature(commandHandle, connectionHandle, data, dataLength, signature, signatureLength, vcxConnectionVerifySignatureCB);
        checkResult(future, result);

        return future;
    }

    private static Callback vcxConnectionGetPwDidCB = new Callback() {

        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int xcommand_handle, int err, String pwDid) {

            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(xcommand_handle);
            if (! checkCallback(future, err)) return;

            future.complete(pwDid);
        }
    };

	/**
	 * Retrieves pw_did from Connection object
	 *
	 *
	 * @param connectionHandle Connection handle that identifies pairwise connection
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
	 * @see "Refer to vcxConnectionCreate example for vcxConnectionConnect demo"
	 * @see #vcxConnectionCreate
	 */
	public static CompletableFuture<String> connectionGetPwDid(int connectionHandle) throws VcxException {

        CompletableFuture<String> future = new CompletableFuture<String>();
        int commandHandle = addFuture(future);
        int result = LibVcx.api.vcx_connection_get_pw_did(commandHandle, connectionHandle, vcxConnectionGetPwDidCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxConnectionGetTheirPwDidCB = new Callback() {

        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int xcommand_handle, int err, String theirPwDid) {

            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(xcommand_handle);
            if (! checkCallback(future, err)) return;

            future.complete(theirPwDid);
        }
    };

	/**
	 * Connection get their pw did completable future.
	 *
	 * @param connectionHandle the connection handle
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 */
	public static CompletableFuture<String> connectionGetTheirPwDid(int connectionHandle) throws VcxException {

        CompletableFuture<String> future = new CompletableFuture<String>();
        int commandHandle = addFuture(future);
        int result = LibVcx.api.vcx_connection_get_their_pw_did(commandHandle, connectionHandle, vcxConnectionGetTheirPwDidCB);
        checkResult(result);

        return future;
    }

	private static Callback vcxConnectionInfoCB = new Callback() {
		@SuppressWarnings({"unused", "unchecked"})
		public void callback(int commandHandle, int err, String info) {
			logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], info = [" + info + "]");
			CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
			if (! checkCallback(future, err)) return;
			future.complete(info);
		}
	};

	/**
	 * Connection info completable future.
	 *
	 * @param connectionHandle the connection handle
	 * @return completable future
	 * @throws VcxException Thrown if an error occurs when calling the underlying SDK
	 */
	public static CompletableFuture<String> connectionInfo(int connectionHandle) throws VcxException {
		logger.debug("connectionInfo() called with: connectionHandle = [" + connectionHandle + "]");
		CompletableFuture<String> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);
		int result = LibVcx.api.vcx_connection_info(commandHandle, connectionHandle, vcxConnectionInfoCB);
		checkResult(result);
		return future;
	}
}
