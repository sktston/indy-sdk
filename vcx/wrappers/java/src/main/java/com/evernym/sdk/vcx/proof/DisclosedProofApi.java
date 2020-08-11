package com.evernym.sdk.vcx.proof;

import com.evernym.sdk.vcx.LibVcx;
import com.evernym.sdk.vcx.ParamGuard;
import com.evernym.sdk.vcx.VcxException;
import com.evernym.sdk.vcx.VcxJava;
import com.sun.jna.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
/**
 * <h1>VCX Disclosed Proof API.</h1>
 * VCX Disclosed Proof APIs <br>
 * Javadoc as written by JJ (Referring to libvcx and python wrapper documents)
 *
 * @version 1.1
 * @since   11/08/2020
 */
public class DisclosedProofApi extends VcxJava.API {

    private DisclosedProofApi() {
    }

    private static final Logger logger = LoggerFactory.getLogger("DisclosedProofApi");

    private static Callback vcxProofCreateWithMsgIdCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, int proofHandle, String proofRequest) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], proofHandle = [" + proofHandle + "], proofRequest = [****]");
            CompletableFuture<CreateProofMsgIdResult> future = (CompletableFuture<CreateProofMsgIdResult>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            CreateProofMsgIdResult result = new CreateProofMsgIdResult(proofHandle, proofRequest);
            future.complete(result);
        }
    };
    /**
     * Send a credential offer to user showing what will be included in the actual credential
     *
     * @param sourceId Institution's identification for the proof, should be unique.
     * @param connectionHandle proof request received via "vcx_get_proof_requests"
     * @param msgId id of the message that contains the proof request
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<CreateProofMsgIdResult> proofCreateWithMsgId(
            String sourceId,
            int connectionHandle,
            String msgId
    ) throws VcxException {
        ParamGuard.notNull(sourceId, "sourceId");
        ParamGuard.notNull(msgId, "msgId");
        logger.debug("proofCreateWithMsgId() called with: sourceId = [" + sourceId + "], connectionHandle = [" + connectionHandle + "], msgId = [" + msgId + "]");
        CompletableFuture<CreateProofMsgIdResult> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_disclosed_proof_create_with_msgid(commandHandle, sourceId, connectionHandle, msgId, vcxProofCreateWithMsgIdCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxProofUpdateStateCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, int state) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], state = [" + state + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            future.complete(state);
        }
    };

    /**
     * Checks for any state change in the disclosed proof and updates the state attribute
     *
     * @param proofHandle Credential handle that was provided during creation. Used to identify disclosed proof object.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Integer> proofUpdateState(
            int proofHandle
    ) throws VcxException {
        logger.debug("proofUpdateState() called with: proofHandle = [" + proofHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_disclosed_proof_update_state(commandHandle, proofHandle, vcxProofUpdateStateCB);
        checkResult(result);

        return future;
    }
    /**
     * Checks for any state change from the given message and updates the state attribute
     *
     * @param proofHandle Credential handle that was provided during creation. Used to identify disclosed proof object.
     * @param message message to process for state changes.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Integer> proofUpdateStateWithMessage(
            int proofHandle,
            String message
    ) throws VcxException {
        logger.debug("proofUpdateStateWithMessage() called with: proofHandle = [" + proofHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_disclosed_proof_update_state_with_message(commandHandle, proofHandle, message, vcxProofUpdateStateCB);
        checkResult(result);

        return future;
    }

    private static Callback proofGetRequestsCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, String proofRequests) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], proofRequests = [****]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            future.complete(proofRequests);
        }
    };
    /**
     * Queries agency for all pending proof requests from the given connection.
     *
     * @param connectionHandle Connection to query for proof requests
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<String> proofGetRequests(
            int connectionHandle
    ) throws VcxException {
        logger.debug("proofGetRequests() called with: connectionHandle = [" + connectionHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_disclosed_proof_get_requests(commandHandle, connectionHandle, proofGetRequestsCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxProofGetStateCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, int state) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], state = [" + state + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            future.complete(state);
        }
    };
    /**
     * Get the current state of the disclosed proof object.
     *
     * @param proofHandle Proof handle that was provided during creation. Used to access disclosed proof object
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Integer> proofGetState(
            int proofHandle
    ) throws VcxException {
        logger.debug("proofGetState() called with: proofHandle = [" + proofHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_disclosed_proof_get_state(commandHandle, proofHandle, vcxProofGetStateCB);
        checkResult(result);

        return future;
    }

    /**
     * Releases the disclosed proof object by de-allocating memory
     *
     * @param proofHandle Proof handle that was provided during creation. Used to access disclosed proof object
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static int proofRelease(int proofHandle) throws VcxException {
        ParamGuard.notNull(proofHandle, "proofHandle");
        logger.debug("proofRelease() called with: proofHandle = [" + proofHandle + "]");

        int result = LibVcx.api.vcx_disclosed_proof_release(proofHandle);
        checkResult(result);

        return result;
    }

    private static Callback vcxProofRetrieveCredentialsCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, String matchingCredentials) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], matchingCredentials = [****]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            String result = matchingCredentials;
            future.complete(result);
        }
    };
    /**
     * Get credentials from wallet matching to the proof request associated with proof object
     *
     * @param proofHandle Proof handle that was provided during creation. Used to access disclosed proof object
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<String> proofRetrieveCredentials(
            int proofHandle
    ) throws VcxException {
        logger.debug("proofRetrieveCredentials() called with: proofHandle = [" + proofHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_disclosed_proof_retrieve_credentials(commandHandle, proofHandle, vcxProofRetrieveCredentialsCB);
        checkResult(result);

        return future;
    }


    private static Callback vcxProofGenerateCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            // resolving with no error
            Integer result = 0;
            future.complete(result);
        }
    };
    /**
     * Accept proof request associated with proof object and generates a proof from the selected credentials and self attested attributes
     *
     * @param proofHandle Proof handle that was provided during creation. Used to access disclosed proof object
     * @param selectedCredentials a json string with a credential for each proof request attribute.
     * <pre><span style="color: gray;font-style: italic;">
     *      List of possible credentials for each attribute is returned from vcx_disclosed_proof_retrieve_credentials,
     *          (user needs to select specific credential to use from list of credentials)
     *          {
     *              "attrs":{
     *                  String:{// Attribute key: This may not be the same as the attr name ex. "age_1" where attribute name is "age"
     *                      "credential": {
     *                          "cred_info":{
     *                              "referent":String,
     *                              "attrs":{ String: String }, // ex. {"age": "111", "name": "Bob"}
     *                              "schema_id": String,
     *                              "cred_def_id": String,
     *                              "rev_reg_id":Option<String>,
     *                              "cred_rev_id":Option<String>,
     *                              },
     *                          "interval":Option<{to: Option<u64>, from:: Option<u64>}>
     *                      }, // This is the exact credential information selected from list of
     *                         // credentials returned from vcx_disclosed_proof_retrieve_credentials
     *                      "tails_file": Option<"String">, // Path to tails file for this credential
     *                  },
     *             },
     *            "predicates":{ TODO: will be implemented as part of IS-1095 ticket. }
     *         }
     *      // selected_credentials can be empty "{}" if the proof only contains self_attested_attrs  </span></pre>
     * @param selfAttestedAttributes a json string with attributes self attested by user
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Integer> proofGenerate(
            int proofHandle,
            String selectedCredentials,
            String selfAttestedAttributes
    ) throws VcxException {
        logger.debug("proofGenerate() called with: proofHandle = [" + proofHandle + "], selectedCredentials = [****], selfAttestedAttributes = [****]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_disclosed_proof_generate_proof(commandHandle, proofHandle, selectedCredentials, selfAttestedAttributes, vcxProofGenerateCB);
        checkResult(result);

        return future;
    }


    private static Callback vcxProofSendCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            // resolving with no error
            Integer result = 0;
            future.complete(result);
        }
    };
    /**
     * Send a proof to the connection, called after having received a proof request
     *
     * @param proofHandle Proof handle that was provided during creation. Used to access disclosed proof object
     * @param connectionHandle Connection handle that identifies pairwise connection
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Integer> proofSend(
            int proofHandle,
            int connectionHandle
    ) throws VcxException {
        logger.debug("proofSend() called with: proofHandle = [" + proofHandle + "], connectionHandle = [" + connectionHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_disclosed_proof_send_proof(commandHandle, proofHandle, connectionHandle, vcxProofSendCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxProofRejectCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            // resolving with no error
            Integer result = 0;
            future.complete(result);
        }
    };
    /**
     * Send a proof rejection to the connection, called after having received a proof request
     *
     * @param proofHandle Proof handle that was provided during creation. Used to access disclosed proof object
     * @param connectionHandle Connection handle that identifies pairwise connection
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Integer> proofReject(
            int proofHandle,
            int connectionHandle
    ) throws VcxException {
        logger.debug("proofReject() called with: proofHandle = [" + proofHandle + "], connectionHandle = [" + connectionHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_disclosed_proof_reject_proof(commandHandle, proofHandle, connectionHandle, vcxProofSendCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxProofGetMsgCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, String msg) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], msg = [" + msg + "]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            future.complete(msg);
        }
    };
    /**
     * Get the proof message for sending.
     *
     * @param proofHandle Proof handle that was provided during creation. Used to access disclosed proof object
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<String> getProofMsg(
            int proofHandle
    ) throws VcxException {
        logger.debug("getProofMsg() called with: proofHandle = [" + proofHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_disclosed_proof_get_proof_msg(commandHandle, proofHandle, vcxProofGetMsgCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxProofGetRejectMsgCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, String msg) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], msg = [" + msg + "]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            future.complete(msg);
        }
    };
    /**
     * Get the reject proof message for sending.
     *
     * @param proofHandle Proof handle that was provided during creation. Used to access disclosed proof object
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<String> getRejectMsg(
            int proofHandle
    ) throws VcxException {
        logger.debug("getRejectMsg() called with: proofHandle = [" + proofHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_disclosed_proof_get_reject_msg(commandHandle, proofHandle, vcxProofGetMsgCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxProofCreateWithRequestCB = new Callback() {
        public void callback(int command_handle, int err, int proofHandle) {
            logger.debug("callback() called with: command_handle = [" + command_handle + "], err = [" + err + "], proofHandle = [" + proofHandle + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(command_handle);
            if(!checkCallback(future, err)) return;
            // resolving with no error
            Integer result = proofHandle;
            future.complete(result);
        }
    };
    /**
     * Create a Proof object for fulfilling a corresponding proof request.
     *
     * @param sourceId Institution's identification for the proof, should be unique.
     * @param proofRequest proof request received via "vcx_get_proof_requests"
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Integer> proofCreateWithRequest(
            String sourceId,
            String proofRequest
    ) throws VcxException {
        ParamGuard.notNull(sourceId, "sourceId");
        ParamGuard.notNull(proofRequest, "proofRequest");
        logger.debug("proofCreateWithRequest() called with: sourceId = [" + sourceId + "], proofRequest = [****]");
        CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_disclosed_proof_create_with_request(commandHandle, sourceId, proofRequest, vcxProofCreateWithRequestCB);
        checkResult(result);

        return future;
    }


    private static Callback vcxProofSerializeCB = new Callback() {
        public void callback(int command_handle, int err, String serializedProof) {
            logger.debug("callback() called with: command_handle = [" + command_handle + "], err = [" + err + "], serializedProof = [****]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(command_handle);
            if(!checkCallback(future, err)) return;

            future.complete(serializedProof);
        }
    };
    /**
     * Takes the disclosed proof object and returns a json string of all its attributes.
     *
     * @param proofHandle Proof handle that was provided during creation. Used to identify the disclosed proof object.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<String> proofSerialize(
            int proofHandle
    ) throws VcxException {
        logger.debug("proofSerialize() called with: proofHandle = [" + proofHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<String>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_disclosed_proof_serialize(commandHandle, proofHandle, vcxProofSerializeCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxProofDeserializeCB = new Callback() {
        public void callback(int command_handle, int err, int proofHandle) {
            logger.debug("callback() called with: command_handle = [" + command_handle + "], err = [" + err + "], proofHandle = [" + proofHandle + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(command_handle);
            if(!checkCallback(future, err)) return;

            future.complete(proofHandle);
        }
    };
    /**
     * Takes a json string representing an disclosed proof object and recreates an object matching the json.
     *
     * @param serializedProof json string representing a disclosed proof object.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Integer> proofDeserialize(
            String serializedProof
    ) throws VcxException {
        ParamGuard.notNull(serializedProof, "serializedProof");
        logger.debug("proofDeserialize() called with: serializedProof = [****]");
        CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_disclosed_proof_deserialize(commandHandle, serializedProof, vcxProofDeserializeCB);
        checkResult(result);

        return future;
    }

	private static Callback vcxDeclinePresentationRequestCB = new Callback() {
		public void callback(int command_handle, int err) {
			logger.debug("callback() called with: command_handle = [" + command_handle + "], err = [" + err + "]");
			CompletableFuture<Void> future = (CompletableFuture<Void>) removeFuture(command_handle);
			if (! checkCallback(future, err)) return;

			future.complete(null);
		}
	};
    /**
     * Declines presentation request.<br>
     * There are two ways of following interaction: <br>
     *      - Prover wants to propose using a different presentation - pass `proposal` parameter. <br>
     *      - Prover doesn't want to continue interaction - pass `reason` parameter. <br>
     * Note that only one of these parameters can be passed. <br>
     *
     * Note that proposing of different presentation is supported for `aries` protocol only.
     *
     * @param proofHandle Proof handle that was provided during creation. Used to identify the disclosed proof object.
     * @param connectionHandle Connection handle that identifies pairwise connection.
     * @param reason (Optional) human-readable string that explain the reason of decline.
     * @param proposal (Optional) the proposed format of presentation request
     * <pre><span style="color: gray;font-style: italic;"> {
     *     "attributes": [
     *         {
     *             "name": "<attribute_name>",
     *             "cred_def_id": Optional("<cred_def_id>"),
     *             "mime-type": Optional("<type>"),
     *             "value": Optional("<value>")
     *         },
     *         // more attributes
     *     ],
     *     "predicates": [
     *         {
     *             "name": "<attribute_name>",
     *             "cred_def_id": Optional("<cred_def_id>"),
     *             "predicate": "<predicate>", - one of "<", "<=", ">=", ">"
     *             "threshold": <threshold>
     *         },
     *         // more predicates
     *     ]
     *  } </span></pre>
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
	public static CompletableFuture<Void> proofDeclineRequest(
			int proofHandle,
			int connectionHandle,
			String reason,
			String proposal
	) throws VcxException {
		logger.debug("declinePresentationRequest() called with: proofHandle = [" + proofHandle + "], connectionHandle = [" + connectionHandle + "], " +
				"reason = [" + reason + "], proposal = [" + proposal + "]");
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_disclosed_proof_decline_presentation_request(commandHandle, proofHandle, connectionHandle, reason, proposal, vcxDeclinePresentationRequestCB);
		checkResult(result);

		return future;
	}
}
