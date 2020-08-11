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
 * <h1>VCX Credential Definition API.</h1>
 * VCX Credential Definition APIs <br>
 * Javadoc as written by JJ (Referring to libvcx and python wrapper documents)
 *
 * @version 1.1
 * @since   11/08/2020
 */
public class ProofApi extends VcxJava.API {
    private ProofApi(){}

    private static final Logger logger = LoggerFactory.getLogger("ProofApi");
    private static Callback vcxProofCreateCB = new Callback() {
        public void callback(int commandHandle, int err, int proofHandle){
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], proofHandle = [" + proofHandle + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if(!checkCallback(future,err)) return;
            Integer result = proofHandle;
            future.complete(result);
        }
    };

    /**
     * Create a new Proof object that requests a proof for an enterprise <br>
     *
     * @param sourceId Enterprise's personal identification for the user.
     * @param requestedAttrs  Describes requested attribute
     * <pre><span style="color: gray;font-style: italic;"> example :
     *   {
     *    "name": Optional<string>, // attribute name, (case insensitive and ignore spaces)
     *    "names": Optional<[string, string]>, // attribute names, (case insensitive and ignore spaces)
     *                                         // NOTE: should either be "name" or "names", not both and not none of them.
     *                                         // Use "names" to specify several attributes that have to match a single credential.
     *    "restrictions":  Optional<wql query> - set of restrictions applying to requested credentials. (see below)
     *    "non_revoked": {
     *        "from": Optional<(u64)> Requested time represented as a total number of seconds from Unix Epoch, Optional
     *        "to": Optional<(u64)>
     *            //Requested time represented as a total number of seconds from Unix Epoch, Optional
     *         }
     *    }
     *    Example requested_attrs -> "[{"name":"attrName","restrictions":["issuer_did":"did","schema_id":"id","schema_issuer_did":"did","schema_name":"name","schema_version":"1.1.1","cred_def_id":"id"}]]"
     *    </span></pre>
     * @param requestedPredicates predicate specifications prover must provide claim for
     * <pre><span style="color: gray;font-style: italic;"> example :
     *   { // set of requested predicates
     *    "name": attribute name, (case insensitive and ignore spaces)
     *    "p_type": predicate type (Currently ">=" only)
     *    "p_value": int predicate value
     *    "restrictions":  Optional<wql query> -  set of restrictions applying to requested credentials. (see below)
     *    "non_revoked": Optional<{
     *        "from": Optional<(u64)> Requested time represented as a total number of seconds from Unix Epoch, Optional
     *        "to": Optional<(u64)> Requested time represented as a total number of seconds from Unix Epoch, Optional
     *       }>
     *    },
     *   Example requested_predicates -> "[{"name":"attrName","p_type":"GE","p_value":9,"restrictions":["issuer_did":"did","schema_id":"id","schema_issuer_did":"did","schema_name":"name","schema_version":"1.1.1","cred_def_id":"id"}]]"
     * </span></pre>
     * @param revocationInterval Optional<<revocation_interval>>, interval applied to all requested attributes indicating when the claim must be valid (NOT revoked) // see below,
     * <pre><span style="color: gray;font-style: italic;"> example :
     *   If specified, prover must proof non-revocation
     *                         // for date in this interval for each attribute
     *                         // (can be overridden on attribute level)
     *   from: Optional<u64> // timestamp of interval beginning
     *   to: Optional<u64> // timestamp of interval beginning
     *         // Requested time represented as a total number of seconds from Unix Epoch, Optional
     *  # Examples config ->  "{}" | "{"to": 123} | "{"from": 100, "to": 123}"
     * </span></pre>
     * @return Proof Object
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * <pre><span style="color: gray;font-style: italic;">
     *   Example:
     *
     *   // Get Credential
     *         String vcxConfigRecord = WalletApi.{@link com.evernym.sdk.vcx.wallet.WalletApi#getRecordWallet getRecordWallet}("vcxConfig", "defaultVcxConfig", "").get();
     *         String vcxConfig = JsonPath.read(vcxConfigRecord,"$.value");
     *
     *         String proofAttributes = JsonPath.parse("[" +
     *                 "  {" +
     *                 "    names: ['name', 'last_name']," +
     *                 "    restrictions: [{ issuer_did: " + JsonPath.read(vcxConfig, "$.institution_did") + " }]" +
     *                 "  }," +
     *                 "  {" +
     *                 "    name: 'date'," +
     *                 "    restrictions: { issuer_did: " + JsonPath.read(vcxConfig, "$.institution_did") + " }" +
     *                 "  }," +
     *                 "  {" +
     *                 "    name: 'degree'," +
     *                 "    restrictions: { 'attr::degree::value': 'maths' }" +
     *                 "  }" +
     *                 "]").jsonString();
     *
     *         String proofPredicates = JsonPath.parse("[" +
     *                 "  {" +
     *                 "    name: 'age'," +
     *                 "    p_type: '>='," +
     *                 "    p_value: 20," +
     *                 "    restrictions: [{ issuer_did: " + JsonPath.read(vcxConfig, "$.institution_did") + " }]" +
     *                 "  }" +
     *                 "]").jsonString();
     *
     *         long curUnixTime = System.currentTimeMillis() / 1000L;
     *         String revocationInterval = "{\"to\": " + curUnixTime + "}";
     *
     *   // Create a Proof object
     *                 "proofAttributes: " + prettyJson(proofAttributes) + "\n" +
     *                 "proofPredicates: " + prettyJson(proofPredicates) + "\n" +
     *                 "revocationInterval: " + prettyJson(revocationInterval));
     *         int proofHandle = ProofApi.{@link #proofCreate proofCreate}("proof_uuid",
     *                 proofAttributes,
     *                 proofPredicates,
     *                 revocationInterval,
     *                 "proof_from_alice").get();
     *
     *   // Request proof of degree from alice
     *         ProofApi.{@link #proofSendRequest proofSendRequest}(proofHandle, connectionHandle).get();
     *
     *         String serializedProof = ProofApi.{@link #proofSerialize proofSerialize}(proofHandle).get();
     *         String threadId = JsonPath.read(serializedProof,"$.data.verifier_sm.state.PresentationRequestSent.presentation_request.@id");
     *   // addRecordWallet
     *         WalletApi.{@link com.evernym.sdk.vcx.wallet.WalletApi#addRecordWallet addRecordWallet}("proof", threadId, serializedProof, "").get();
     *   // Proof Release
     *         ProofApi.{@link #proofRelease proofRelease}(proofHandle);
     * </span></pre>
     * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/master/src/main/java/webhook/faber/GlobalService.java" target="_blank">VCX JAVA Demo - Proof Create Example</a>
     *
     */
    public static CompletableFuture<Integer> proofCreate(
            String sourceId,
            String requestedAttrs,
            String requestedPredicates,
            String revocationInterval,
            String name
    ) throws VcxException {
        ParamGuard.notNull(sourceId, "sourceId");
        ParamGuard.notNull(requestedAttrs, "requestedAttrs");
        ParamGuard.notNull(requestedPredicates, "requestedPredicates");
        ParamGuard.notNull(revocationInterval, "revocationInterval");
        ParamGuard.notNull(name, "name");
        logger.debug("proofCreate() called with: sourceId = [" + sourceId + "], requestedAttrs = [" + requestedAttrs + "], requestedPredicates = [" + requestedPredicates + "], revocationInterval = [" + revocationInterval + "], name = [" + name + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);
        if (requestedPredicates.isEmpty()) requestedPredicates = "[]";
        int result = LibVcx.api.vcx_proof_create(commandHandle, sourceId, requestedAttrs, requestedPredicates, revocationInterval, name, vcxProofCreateCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxProofSendRequestCB = new Callback() {
        public void callback(int commandHandle, int err){
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if(!checkCallback(future,err)) return;
            Integer result = commandHandle;
            future.complete(result);
        }
    };

    /**
     * Sends a proof request to pairwise connection
     *
     * @param proofHandle Proof handle that was provided during creation. Used to access proof object.
     * @param connectionHandle Connection handle that identifies pairwise connection.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer to proofCreate example for credential demo"
     * @see #proofCreate
     */
    public static CompletableFuture<Integer> proofSendRequest(
            int proofHandle,
            int connectionHandle
    ) throws VcxException {
        ParamGuard.notNull(proofHandle, "proofHandle");
        ParamGuard.notNull(connectionHandle, "connectionHandle");
        logger.debug("proofSendRequest() called with: proofHandle = [" + proofHandle + "], connectionHandle = [" + connectionHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_proof_send_request(commandHandle, proofHandle, connectionHandle, vcxProofSendRequestCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxProofGetRequestMsgCB = new Callback() {
        public void callback(int commandHandle, int err, String msg){
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], msg = [" + msg + "]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
            if(!checkCallback(future,err)) return;
            Integer result = commandHandle;
            future.complete(msg);
        }
    };
    /**
     * Get the proof request message that can be sent to the specified connection
     *
     * @param proofHandle Proof handle that was provided during creation. Used to access proof object
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<String> proofGetRequestMsg(
            int proofHandle
    ) throws VcxException {
        ParamGuard.notNull(proofHandle, "proofHandle");
        logger.debug("proofGetRequestMsg() called with: proofHandle = [" + proofHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_proof_get_request_msg(commandHandle, proofHandle, vcxProofGetRequestMsgCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxGetProofCB = new Callback() {
        public void callback(int commandHandle, int err, int proofState, String responseData){
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], proofState = [" + proofState + "], responseData = [****]");
            CompletableFuture<GetProofResult> future = (CompletableFuture<GetProofResult>) removeFuture(commandHandle);
            if(!checkCallback(future,err)) return;
            GetProofResult result = new GetProofResult(proofState,responseData);
            future.complete(result);
        }
    };

    @Deprecated
    public static CompletableFuture<GetProofResult> getProof(
            int proofHandle,
            int connectionHandle
    ) throws VcxException {
        ParamGuard.notNull(proofHandle, "proofHandle");
        ParamGuard.notNull(connectionHandle, "connectionHandle");
        logger.debug("getProof() called with: proofHandle = [" + proofHandle + "], connectionHandle = [" + connectionHandle + "]");
        CompletableFuture<GetProofResult> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_get_proof(commandHandle, proofHandle, connectionHandle, vcxGetProofCB);
        checkResult(result);

        return future;
    }

    /**
     * Get proof message<br>
     * This replaces {@link #getProof getProof}.
     *
     * @param proofHandle Proof handle that was provided during creation. Used to identify proof object.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/master/src/main/java/webhook/faber/GlobalService.java" target="_blank">VCX JAVA Demo - Proof Create Example</a>
     *
     */
    public static CompletableFuture<GetProofResult> getProofMsg(
            int proofHandle
    ) throws VcxException {
        ParamGuard.notNull(proofHandle, "proofHandle");
        logger.debug("getProof() called with: proofHandle = [" + proofHandle + "]");
        CompletableFuture<GetProofResult> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_get_proof_msg(commandHandle, proofHandle, vcxGetProofCB);
        checkResult(result);

        return future;
    }


    // vcx_proof_accepted
    /**
     * proof Accept<br>
     *
     * @param proofHandle Proof handle that was provided during creation. Used to identify proof object.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     *
     */
    public static CompletableFuture<Integer> proofAccepted(
            int proofHandle,
            String responseData
    ) throws VcxException {
        ParamGuard.notNull(proofHandle, "proofHandle");
        ParamGuard.notNull(responseData, "responseData");
        logger.debug("proofAccepted() called with: proofHandle = [" + proofHandle + "], responseData = [****]");
        CompletableFuture<Integer> future = new CompletableFuture<>();

        int result = LibVcx.api.vcx_proof_accepted(proofHandle, responseData);
        checkResult(result);

        return future;
    }

    private static Callback vcxProofUpdateStateCB = new Callback() {
        public void callback(int commandHandle, int err, int state){
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], state = [" + state + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if(!checkCallback(future,err)) return;
            Integer result = state;
            future.complete(result);
        }
    };

    /**
     * Query the agency for the received messages <br>
     * Checks for any messages changing state in the object and updates the state attribute.<br>
     *
     * @param proofHandle Proof handle that was provided during creation. Used to access proof object.
     * @return provides most current state of the credential and error status of request
     * <pre><span style="color: gray;font-style: italic;"> States:
     *  1 - Initialized
     *  2 - Request Sent
     *  3 - Proof Received
     *  4 - Accepted </span></pre>
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * <pre><span style="color: gray;font-style: italic;">
     *   Example:
     *     // get proof
     *         String threadId = JsonPath.read(payloadMessage, "$.~thread.thid");
     *         String proofRecord = WalletApi..{@link com.evernym.sdk.vcx.wallet.WalletApi#getRecordWallet getRecordWallet}("proof", threadId, "").get();
     *         String serializedProof = JsonPath.read(proofRecord, "$.value");
     *         serializedProof = JsonPath.parse(serializedProof)
     *                 .set("$.data.verifier_sm.state.PresentationRequestSent.connection_handle", Integer.toUnsignedLong(connectionHandle))
     *                 .jsonString();
     *     // Proof Deserialize
     *         int proofHandle = ProofApi.{@link #proofDeserialize proofDeserialize}(serializedProof).get();
     *     // Proof Update State
     *         int proofState = ProofApi.{@link #proofUpdateState proofUpdateState}(proofHandle).get();
     * </span></pre>
     * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/master/src/main/java/webhook/faber/GlobalService.java" target="_blank">VCX JAVA Demo - Proof update state Example</a>
     *
     */
    public static CompletableFuture<Integer> proofUpdateState(
            int proofHandle
    ) throws VcxException {
        ParamGuard.notNull(proofHandle, "proofHandle");
        logger.debug("proofUpdateState() called with: proofHandle = [" + proofHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_proof_update_state(commandHandle, proofHandle, vcxProofUpdateStateCB);
        checkResult(result);

        return future;
    }
    /**
     * Update the state of the proof based on the given message.
     *
     * @param proofHandle Proof handle that was provided during creation. Used to access proof object
     * @return provides most current state of the credential and error status of request
     * <pre><span style="color: gray;font-style: italic;"> States:
     *  1 - Initialized
     *  2 - Request Sent
     *  3 - Proof Received
     *  4 - Accepted </span></pre>
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Integer> proofUpdateStateWithMessage(
            int proofHandle,
            String message
    ) throws VcxException {
        ParamGuard.notNull(proofHandle, "proofHandle");
        logger.debug("proofUpdateStateWithMessage() called with: proofHandle = [" + proofHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_proof_update_state_with_message(commandHandle, proofHandle, message, vcxProofUpdateStateCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxProofGetStateCB = new Callback() {
        public void callback(int commandHandle, int err, int state){
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], state = [" + state + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if(!checkCallback(future,err)) return;
            Integer result = state;
            future.complete(result);
        }
    };
    /**
     * Get the current state of the proof object
     *
     * @param proofHandle Proof handle that was provided during creation. Used to access proof object
     * @return provides most current state of the credential and error status of request
     * <pre><span style="color: gray;font-style: italic;"> States:
     *  1 - Initialized
     *  2 - Request Sent
     *  3 - Proof Received
     *  4 - Accepted </span></pre>
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Integer> proofGetState(
            int proofHandle
    ) throws VcxException {
        ParamGuard.notNull(proofHandle, "proofHandle");
        logger.debug("proofGetState() called with: proofHandle = [" + proofHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_proof_get_state(commandHandle, proofHandle, vcxProofGetStateCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxProofSerializeCB = new Callback() {
        public void callback(int commandHandle, int err, String proofState){
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], proofState = [" + proofState + "]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
            if(!checkCallback(future,err)) return;
            future.complete(proofState);
        }
    };
    /**
     * Takes the proof object and returns a json string of all its attributes
     *
     * @param proofHandle Proof handle that was provided during creation. Used to access proof object.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer to proofCreate example for credential demo"
     * @see #proofCreate
     */
    public static CompletableFuture<String> proofSerialize(
            int proofHandle
    ) throws VcxException {
        ParamGuard.notNull(proofHandle, "proofHandle");
        logger.debug("proofSerialize() called with: proofHandle = [" + proofHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_proof_serialize(commandHandle, proofHandle, vcxProofSerializeCB);
        checkResult(result);

        return future;
    }

    private static Callback vcxProofDeserializeCB = new Callback() {
        public void callback(int commandHandle, int err, int proofHandle){
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], proofHandle = [" + proofHandle + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if(!checkCallback(future,err)) return;
            Integer result = proofHandle;
            future.complete(result);
        }
    };

    /**
     * Takes a json string representing a proof object and recreates an object matching the json
     *
     * @param serializedProof json string representing a proof object
     * @return Success
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer to proofCreate example for credential demo"
     * @see #proofCreate
     */
    public static CompletableFuture<Integer> proofDeserialize(
            String serializedProof
    ) throws VcxException {
        ParamGuard.notNull(serializedProof, "serializedProof");
        logger.debug("proofDeserialize() called with: serializedProof = [****]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_proof_deserialize(commandHandle, serializedProof, vcxProofDeserializeCB);
        checkResult(result);

        return future;
    }

    /**
     * Releases the proof object by de-allocating memory
     *
     * @param proofHandle Proof handle that was provided during creation. Used to access proof object.
     * @return Success
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer to proofCreate example for credential demo"
     * @see #proofCreate
     */
    public static int proofRelease(int proofHandle) throws VcxException {
        ParamGuard.notNull(proofHandle, "proofHandle");
        logger.debug("proofRelease() called with: proofHandle = [" + proofHandle + "]");

        int result = LibVcx.api.vcx_proof_release(proofHandle);
        checkResult(result);

        return result;
    }

}
