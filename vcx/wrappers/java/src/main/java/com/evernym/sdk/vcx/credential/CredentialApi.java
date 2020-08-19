package com.evernym.sdk.vcx.credential;

import com.evernym.sdk.vcx.LibVcx;
import com.evernym.sdk.vcx.ParamGuard;
import com.evernym.sdk.vcx.VcxException;
import com.evernym.sdk.vcx.VcxJava;
import com.sun.jna.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
/**
 * <h1>VCX Credential API.</h1>
 * VCX Credential APIs <br>
 * Javadoc as written by JJ (Referring to libvcx and python wrapper documents)
 *
 * @version 1.1
 * @since   11/08/2020
 */
public class CredentialApi extends VcxJava.API {

    private static final Logger logger = LoggerFactory.getLogger("CredentialApi");
    private CredentialApi() {
    }

    private static Callback vcxCredentialCreateWithMsgidCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int command_handle, int err, int credentialHandle, String offer) {
            logger.debug("callback() called with: command_handle = [" + command_handle + "], err = [" + err + "], credentialHandle = [" + credentialHandle + "], offer = [****]");
            CompletableFuture<GetCredentialCreateMsgidResult> future = (CompletableFuture<GetCredentialCreateMsgidResult>) removeFuture(command_handle);
            if (!checkCallback(future, err)) return;
            GetCredentialCreateMsgidResult result = new GetCredentialCreateMsgidResult(credentialHandle, offer);
            future.complete(result);
        }
    };


    /**
     * Create a credential based off of a known message id for a given connection.<br>
     *
     * @param sourceId Institution's personal identification for the credential, should be unique.
     * @param connectionHandle connection to query for credential offer
     * @param msgId msgid that contains the credential offer
     * @return A created credential
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK
     *
     */
    public static CompletableFuture<GetCredentialCreateMsgidResult> credentialCreateWithMsgid(
            String sourceId,
            int connectionHandle,
            String msgId
    ) throws VcxException {
        ParamGuard.notNullOrWhiteSpace(sourceId, "sourceId");
        ParamGuard.notNullOrWhiteSpace(msgId, "msgId");
        logger.debug("credentialCreateWithMsgid() called with: sourceId = [" + sourceId + "], connectionHandle = [" + connectionHandle + "], msgId = [" + msgId + "]");
        CompletableFuture<GetCredentialCreateMsgidResult> future = new CompletableFuture<GetCredentialCreateMsgidResult>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_credential_create_with_msgid(
                commandHandle,
                sourceId,
                connectionHandle,
                msgId,
                vcxCredentialCreateWithMsgidCB);
        checkResult(result);

        return future;

    }

    private static Callback vcxCredentialSendRequestCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int command_handle, int err) {
            logger.debug("callback() called with: command_handle = [" + command_handle + "], err = [" + err + "]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(command_handle);
            if (!checkCallback(future,err)) return;
            // returning empty string from here because we don't want to complete future with null
            future.complete("");
        }
    };


    /**
     * Approves the credential offer and submits a credential request.<br>
     * The result will be a credential stored in the prover's wallet.<br>
     *
     * @param credentialHandle credential handle that was provided during creation. Used to identify credential object.
     * @param connectionHandle Connection handle that identifies pairwise connection
     * @param paymentHandle currently unused
     * @return A created credential
     * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/a25a25d652aff2eaea0d3075ac17e1e3f35d621b/src/main/java/webhook/alice/GlobalService.java" target="_blank">VCX JAVA Demo - Credential offer Example</a>
     *
     */
    public static CompletableFuture<String> credentialSendRequest(
            int credentialHandle,
            int connectionHandle,
            int paymentHandle
    ) throws VcxException {
        logger.debug("credentialSendRequest() called with: credentialHandle = [" + credentialHandle + "], connectionHandle = [" + connectionHandle + "], paymentHandle = [" + paymentHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<String>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_credential_send_request(
                commandHandle,
                credentialHandle,
                connectionHandle,
                paymentHandle,
                vcxCredentialSendRequestCB);
        checkResult(result);

        return future;

    }


    /**
     * Approves the credential offer and gets the credential request message that can be sent to the specified connection.<br>
     *
     * @param credentialHandle credential handle that was provided during creation. Used to identify credential object
     * @param myPwDid Use Connection api (vcx_connection_get_pw_did) with specified connection_handle to retrieve your pw_did
     * @param theirPwDid Use Connection api (vcx_connection_get_their_pw_did) with specified connection_handle to retrieve theri pw_did
     * @param paymentHandle currently unused
     * @return A created credential
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK
     *
     */
    public static CompletableFuture<String> credentialGetRequestMsg(
            int credentialHandle,
            String myPwDid,
            String theirPwDid,
            int paymentHandle
    ) throws VcxException {
        logger.debug("credentialGetRequestMsg() called with: credentialHandle = [" + credentialHandle + "], myPwDid = [" + myPwDid + "], theirPwDid = [" + theirPwDid + "], paymentHandle = [" + paymentHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<String>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_credential_get_request_msg(
                commandHandle,
                credentialHandle,
                myPwDid,
                theirPwDid,
                paymentHandle,
                vcxCredentialStringCB);
        checkResult(result);

        return future;

    }

    private static Callback vcxCredentialStringCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int command_handle, int err, String stringData) {
            logger.debug("callback() called with: command_handle = [" + command_handle + "], err = [" + err + "], string = [" + stringData + "]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(command_handle);
            if (!checkCallback(future, err)) return;
            future.complete(stringData);
        }
    };

    /**
     * Takes the credential object and returns a json string of all its attributes
     *
     * @param credentailHandle Credential handle that was provided during creation. Used to identify credential object.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer to credentialSendRequest example for credential demo"
     * @see #credentialSendRequest
     */
    public static CompletableFuture<String> credentialSerialize(
            int credentailHandle
    ) throws VcxException {
        logger.debug("credentialSerialize() called with: credentailHandle = [" + credentailHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<String>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_credential_serialize(commandHandle,
                credentailHandle,
                vcxCredentialStringCB);
        checkResult(result);

        return future;

    }

    private static Callback vcxCredentialDeserializeCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int command_handle, int err, int credentialHandle) {
            logger.debug("callback() called with: command_handle = [" + command_handle + "], err = [" + err + "], credentialHandle = [" + credentialHandle + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(command_handle);
            if (!checkCallback(future, err)) return;
            Integer result = credentialHandle;
            future.complete(result);
        }
    };


    /**
     * Takes a json string representing an credential object and recreates an object matching the json <br>
     *
     * @param serializedCredential json string representing a credential object.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK
     * <pre><span style="color: gray;font-style: italic;">
     *   Example:
     *   // Accept credential
     *         String threadId = JsonPath.read(payloadMessage, "$.~thread.thid");
     *         String credentialRecord = WalletApi.{@link com.evernym.sdk.vcx.wallet.WalletApi#getRecordWallet getRecordWallet}("credential", threadId, "").get();
     *         String serializedCredential = JsonPath.read(credentialRecord, "$.value");
     *   // Deserialize Credential
     *         serializedCredential = JsonPath.parse(serializedCredential)
     *                 .set("$.data.holder_sm.state.RequestSent.connection_handle", Integer.toUnsignedLong(connectionHandle))
     *                 .jsonString();
     *         int credentialHandle = CredentialApi.{@link #credentialDeserialize credentialDeserialize}(serializedCredential).get();
     *   // Credential Update State
     *         int credentialState = CredentialApi.{@link #credentialUpdateState credentialUpdateState}(credentialHandle).get();
     *
     *   // Serialize the object
     *         serializedCredential = CredentialApi.{@link #credentialSerialize credentialSerialize}(credentialHandle).get();
     *   // Update Record Wallet
     *         WalletApi.{@link com.evernym.sdk.vcx.wallet.WalletApi#updateRecordWallet updateRecordWallet}("credential", threadId, serializedCredential).get();
     *   // Credential Release
     *         CredentialApi.{@link #credentialRelease credentialRelease}(credentialHandle);
     * </span></pre>
     * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/master/src/main/java/webhook/alice/GlobalService.java" target="_blank">VCX JAVA Demo - Credential offer Example</a>
     *
     */
    public static CompletableFuture<Integer> credentialDeserialize(
            String serializedCredential
    ) throws VcxException {
        ParamGuard.notNull(serializedCredential, "serializedCredential");
        logger.debug("credentialDeserialize() called with: serializedCredential = [****]");
        CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_credential_deserialize(commandHandle,
                serializedCredential,
                vcxCredentialDeserializeCB);
        checkResult(result);

        return future;

    }

    private static Callback vcxGetCredentialCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int command_handle, int err, String credential) {
            logger.debug("callback() called with: command_handle = [" + command_handle + "], err = [" + err + "], credential = [****]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(command_handle);
            if (!checkCallback(future, err)) return;
            future.complete(credential);
        }
    };


    /**
     * Retrieve information about a stored credential in user's wallet, including credential id and the credential itself.
     *
     * @param credentialHandle credential handle that was provided during creation. Used to identify credential object.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<String> getCredential(
            int credentialHandle
    ) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        logger.debug("getCredential() called with: credentialHandle = [" + credentialHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<String>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_get_credential(commandHandle, credentialHandle, vcxGetCredentialCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxDeleteCredentialCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int command_handle, int err) {
            logger.debug("callback() called with: command_handle = [" + command_handle + "], err = [" + err + "]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(command_handle);
            if (!checkCallback(future,err)) return;
            // returning empty string from here because we don't want to complete future with null
            future.complete("");
        }
    };


    /**
     * Delete a Credential from the wallet and release its handle.
     *
     * @param credentialHandle handle of the credential to delete.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<String> deleteCredential(
            int credentialHandle
    ) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        logger.debug("deleteCredential() called with: credentialHandle = [" + credentialHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<String>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_delete_credential(commandHandle, credentialHandle, vcxDeleteCredentialCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxCredentialUpdateStateCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int command_handle, int err, int state) {
            logger.debug("callback() called with: command_handle = [" + command_handle + "], err = [" + err + "], state = [" + state + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(command_handle);
            if (!checkCallback(future, err)) return;
            Integer result = state;
            future.complete(result);
        }
    };

    /**
     * Query the agency for the received messages.
     * Checks for any messages changing state in the credential object and updates the state attribute.
     * If it detects a credential it will store the credential in the wallet.
     *
     * @param credentialHandle Credential handle that was provided during creation. Used to identify credential object.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer to credentialSendRequest example for credential demo"
     * @see #credentialSendRequest
     */
    public static CompletableFuture<Integer> credentialUpdateState(
            int credentialHandle
    ) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        logger.debug("credentialUpdateState() called with: credentialHandle = [" + credentialHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_credential_update_state(commandHandle, credentialHandle, vcxCredentialUpdateStateCB);
        checkResult(result);

        return future;
    }

    /**
     * Update the state of the credential based on the given message.
     *
     * @param credentialHandle Credential handle that was provided during creation. Used to identify credential object.
     * @param message message to process for state changes.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Integer> credentialUpdateStateWithMessage(
            int credentialHandle,
            String message
    ) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        logger.debug("credentialUpdateState() called with: credentialHandle = [" + credentialHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_credential_update_state_with_message(commandHandle, credentialHandle, message, vcxCredentialUpdateStateCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxCredentialGetStateCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int command_handle, int err, int state) {
            logger.debug("callback() called with: command_handle = [" + command_handle + "], err = [" + err + "], state = [" + state + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(command_handle);
            if (!checkCallback(future, err)) return;
            Integer result = state;
            future.complete(result);
        }
    };

    /**
     * Get the current state of the credential object
     *
     * @param credentialHandle Credential handle that was provided during creation.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Integer> credentialGetState(
            int credentialHandle
    ) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        logger.debug("credentialGetState() called with: credentialHandle = [" + credentialHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_credential_get_state(commandHandle, credentialHandle, vcxCredentialGetStateCB);
        checkResult(result);

        return future;
    }


    /**
     * Releases the credential object by de-allocating memory.
     *
     *
     * @param credentialHandle Credential handle that was provided during creation. Used to access credential object.
     * @return Success
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer to credentialSendRequest example for credential demo"
     * @see #credentialSendRequest
     */
    public static int credentialRelease(int credentialHandle) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        logger.debug("credentialRelease() called with: credentialHandle = [" + credentialHandle + "]");

        int result = LibVcx.api.vcx_credential_release(credentialHandle);
        checkResult(result);

        return result;
    }

    private static Callback vcxCredentialGetOffersCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int command_handle, int err, String credential_offers) {
            logger.debug("callback() called with: command_handle = [" + command_handle + "], err = [" + err + "], credential_offers = [****]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(command_handle);
            if (!checkCallback(future, err)) return;
            future.complete(credential_offers);
        }
    };


    /**
     * Queries agency for credential offers from the given connection.
     *
     *
     * @param connectionHandle Connection to query for credential offers.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer to credentialSendRequest example for credential demo"
     * @see #credentialSendRequest
     */
    public static CompletableFuture<String> credentialGetOffers(
            int connectionHandle
    ) throws VcxException {
        ParamGuard.notNull(connectionHandle, "connectionHandle");
        logger.debug("credentialGetOffers() called with: connectionHandle = [" + connectionHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<String>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_credential_get_offers(commandHandle, connectionHandle, vcxCredentialGetOffersCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxCredentialCreateWithOfferCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int command_handle, int err, int credential_handle) {
            logger.debug("callback() called with: command_handle = [" + command_handle + "], err = [" + err + "], credential_handle = [" + credential_handle + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(command_handle);
            if (!checkCallback(future, err)) return;
            Integer result = credential_handle;
            future.complete(result);
        }
    };

    /**
     * Create a Credential object that requests and receives a credential for an institution
     *
     * @param sourceId Institution's personal identification for the credential, should be unique.
     * @param credentialOffer credential offer received via "vcx_credential_get_offers"
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer to credentialSendRequest example for credential demo"
     * @see #credentialSendRequest
     */
    public static CompletableFuture<Integer> credentialCreateWithOffer(
            String sourceId,
            String credentialOffer
    ) throws VcxException {
        ParamGuard.notNull(sourceId, "sourceId");
        ParamGuard.notNull(credentialOffer, "credentialOffer");
        logger.debug("credentialCreateWithOffer() called with: sourceId = [" + sourceId + "], credentialOffer = [****]");
        CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_credential_create_with_offer(commandHandle, sourceId, credentialOffer, vcxCredentialCreateWithOfferCB);
        checkResult(result);

        return future;
    }
}
