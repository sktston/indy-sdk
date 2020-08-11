package com.evernym.sdk.vcx.issuer;

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
 * Javadoc written by SKTelecom (The original is vcx and python wrapper documents)
 * @author  JJ
 * @version 1.0
 * @since   09/08/2020
 */
public class IssuerApi extends VcxJava.API {

    private static final Logger logger = LoggerFactory.getLogger("IssuerApi");
    private static final Callback issuerCreateCredentialCB = new Callback() {
        // TODO: This callback and jna definition needs to be fixed for this API
        // it should accept connection handle as well
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, int credentialHandle) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], credentialHandle = [" + credentialHandle + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            Integer result = credentialHandle;
            future.complete(result);
        }
    };

    /**
     * Create a Issuer Credential object that provides a credential for an enterprise's user <br>
     * Assumes a credential definition has been already written to the ledger.<br>
     *
     * @param sourceId Enterprise's personal identification for the user.
     * @param credentialDefHandle id of credential definition given during creation of the credential definition
     * @param issuerId did corresponding to entity issuing a credential. Needs to have Trust Anchor permissions on ledger
     * @param credentialData data attributes offered to person in the credential
     * @param credentialName Name of the credential - ex. Drivers Licence
     * @param price price of credential
     * @return A created credential
     * @throws VcxException the vcx exception.
     * <pre><span style="color: gray;font-style: italic;">
     *   Example:
     *
     *   // Create Credential
     *         String schemaAttrs = JsonPath.parse("{" +
     *                 "  name: 'alice'," +
     *                 "  last_name: 'clark'," +
     *                 "  date: '05-2018'," +
     *                 "  degree: 'maths'," +
     *                 "  age: '25'" +
     *                 "}").jsonString();
     *         String credDefRecord = WalletApi.{@link com.evernym.sdk.vcx.wallet.WalletApi#getRecordWallet getRecordWallet}("credentialDef", "defaultCredentialDef", "").get();
     *         String credDef = JsonPath.read(credDefRecord,"$.value");
     *         int credDefHandle = CredentialDefApi.{@link com.evernym.sdk.vcx.credentialDef.CredentialDefApi#credentialDefDeserialize(String) credentialDefDeserialize}(credDef).get();
     *
     *         int credentialHandle = IssuerApi.{@link #issuerCreateCredential issuerCreateCredential}("alice_degree",
     *                 credDefHandle,
     *                 null,
     *                 schemaAttrs,
     *                 "cred",
     *                 0).get();
     *   // Issue credential offer to alice
     *         IssuerApi.{@link #issuerSendCredentialOffer issuerSendCredentialOffer}(credentialHandle, connectionHandle).get();
     *   // credential serialize
     *         String serializedCredential = IssuerApi.issuerCredentialSerialize(credentialHandle).get();
     *         String threadId = JsonPath.read(serializedCredential,"$.data.issuer_sm.state.OfferSent.thread_id");
     *   // addRecordWallet
     *         WalletApi.addRecordWallet("credential", threadId, serializedCredential, "").get();
     *   // Release
     *         IssuerApi.{@link #issuerCredentialRelease issuerCredentialRelease}(credentialHandle);
     *         CredentialDefApi.{@link com.evernym.sdk.vcx.credentialDef.CredentialDefApi#credentialDefRelease credentialDefRelease}(credDefHandle);
     * </span></pre>
     * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/53bda51f7fff5d5379faa680fac10d96253b1302/src/main/java/webhook/faber/GlobalService.java" target="_blank">VCX JAVA Demo - Credential Definition Create Example</a>
     *
     */
    public static CompletableFuture<Integer> issuerCreateCredential(String sourceId,
                                                                    int credentialDefHandle,
                                                                    String issuerId,
                                                                    String credentialData,
                                                                    String credentialName,
                                                                    long price) throws VcxException {
        ParamGuard.notNullOrWhiteSpace(sourceId, "sourceId");
        ParamGuard.notNullOrWhiteSpace(credentialData, "credentialData");
        ParamGuard.notNullOrWhiteSpace(credentialName, "credentialName");

        logger.debug("issuerCreateCredential() called with: sourceId = [" + sourceId + "], credentialDefHandle = [" + credentialDefHandle + "], issuerId = [" + issuerId + "], credentialData = [" + credentialData + "], credentialName = [" + credentialName + "], price = [" + price + "]");
        //TODO: Check for more mandatory params in vcx to add in PamaGuard
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int issue = addFuture(future);

        int result = LibVcx.api.vcx_issuer_create_credential(
                issue,
                sourceId,
                credentialDefHandle,
                issuerId,
                credentialData,
                credentialName,
                String.valueOf(price),
                issuerCreateCredentialCB);
        checkResult(result);
        return future;
    }

    private static Callback issuerSendCredentialOfferCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            // TODO complete with exception if we find error
//            if (err != 0) {
//                future.completeExceptionally();
//            } else {
//
//            }
            future.complete(err);
        }
    };
    /**
     * Send a credential offer to user showing what will be included in the actual credential
     *
     * @param credentialHandle Credential handle that was provided during creation. Used to identify credential object.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer issuerCreateCredential example for credential demo"
     * @see #issuerCreateCredential
     */
    public static CompletableFuture<Integer> issuerSendCredentialOffer(int credentialHandle,
                                                                       int connectionHandle) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        ParamGuard.notNull(connectionHandle, "connectionHandle");
        logger.debug("issuerSendcredentialOffer() called with: credentialOffer = [" + credentialHandle + "], connectionHandle = [" + connectionHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int issue = addFuture(future);

        int result = LibVcx.api.vcx_issuer_send_credential_offer(
                issue,
                credentialHandle,
                connectionHandle,
                issuerSendCredentialOfferCB
        );
        checkResult(result);
        return future;
    }
    /**
     * Gets the offer message that can be sent to the specified connection
     *
     * @param credentialHandle Credential handle that was provided during creation. Used to identify credential object.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer issuerCreateCredential example for credential demo"
     * @see #issuerCreateCredential
     */
    public static CompletableFuture<String> issuerGetCredentialOfferMsg(int credentialHandle) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        logger.debug("issuerSendCredentialOffer() called with: credentialHandle = [****]");
        CompletableFuture<String> future = new CompletableFuture<>();
        int issue = addFuture(future);

        int result = LibVcx.api.vcx_issuer_get_credential_offer_msg(
                issue,
                credentialHandle,
                issuerCredentialStringCB
        );
        checkResult(result);
        return future;
    }

    private static Callback issuerCredentialUpdateStateCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err,int state) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], state = [" + state + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            future.complete(state);
        }
    };

    /**
     * Query the agency for the received messages. <br>
     * Checks for any messages changing state in the object and updates the state attribute.<br>
     *
     * @param credentialHandle Credential handle that was provided during creation. Used to identify credential object.
     * @return provides most current state of the credential and error status of request
     * <pre><span style="color: gray;font-style: italic;"> States:
     *  1 - Initialized
     *  2 - Offer Sent
     *  3 - Request Received
     *  4 - Issued </span></pre>
     * @throws VcxException the vcx exception.
     * <pre><span style="color: gray;font-style: italic;">
     *   Example:
     *
     *   // Get Credential
     *         String threadId = JsonPath.read(payloadMessage,"$.~thread.thid");
     *         String credentialRecord = WalletApi.{@link com.evernym.sdk.vcx.wallet.WalletApi#getRecordWallet getRecordWallet}("credential", threadId, "").get();
     *         String serializedCredential = JsonPath.read(credentialRecord,"$.value");
     *         serializedCredential = JsonPath.parse(serializedCredential)
     *                 .set("$.data.issuer_sm.state.OfferSent.connection_handle", Integer.toUnsignedLong(connectionHandle))
     *                 .jsonString();
     *   // Credential Deserialize
     *         int credentialHandle = IssuerApi.{@link #issuerCredentialDeserialize issuerCredentialDeserialize}(serializedCredential).get();
     *   // Credential Update State
     *         int credentialState = IssuerApi.{@link #issuerCredentialUpdateState issuerCredentialUpdateState}(credentialHandle).get();
     *
     *         if (credentialState == VcxState.RequestReceived.getValue()) {
     *    // Issue credential to alice
     *             IssuerApi.{@link #issuerSendCredential issuerSendCredential}(credentialHandle, connectionHandle).get();
     *             serializedCredential = IssuerApi.{@link #issuerCredentialSerialize issuerCredentialSerialize}(credentialHandle).get();
     *    // updateRecordWallet
     *             WalletApi.updateRecordWallet("credential", threadId, serializedCredential).get();
     *         }
     *         else {
     *             log.severe("Unexpected state type");
     *         }
     *    // Credential Release
     *         IssuerApi.{@link #issuerCredentialRelease issuerCredentialRelease}(credentialHandle);
     * </span></pre>
     * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/53bda51f7fff5d5379faa680fac10d96253b1302/src/main/java/webhook/faber/GlobalService.java" target="_blank">VCX JAVA Demo - Credential Definition Create Example</a>
     *
     */
    public static CompletableFuture<Integer> issuerCredentialUpdateState(int credentialHandle) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        logger.debug("issuerCredentialUpdateState() called with: credentialHandle = [" + credentialHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int issue = addFuture(future);
        int result = LibVcx.api.vcx_issuer_credential_update_state(issue, credentialHandle, issuerCredentialUpdateStateCB);
        checkResult(result);
        return future;
    }

    /**
     * Update the state of the credential based on the given message
     *
     * @param credentialHandle Credential handle that was provided during creation. Used to identify credential object.
     * @param message message to process for state changes.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Integer> issuerCredentialUpdateStateWithMessage(int credentialHandle, String message) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        logger.debug("issuerCredentialUpdateStateWithMessage() called with: credentialHandle = [" + credentialHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int issue = addFuture(future);
        int result = LibVcx.api.vcx_issuer_credential_update_state_with_message(issue, credentialHandle, message, issuerCredentialUpdateStateCB);
        checkResult(result);
        return future;
    }

    private static Callback issuerCredentialGetStateCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, int state) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], state = [" + state + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            future.complete(state);
        }
    };
    /**
     * Get the current state of the issuer credential object <br>
     *
     * @param credentialHandle Issuer Credential handle that was provided during creation..
     * @return provides most current state of the credential definition and error status of request
     * <pre><span style="color: gray;font-style: italic;"> States:
     *  1 - Initialized
     *  2 - Offer Sent
     *  3 - Request Received
     *  4 - Issued</span></pre>
     * @throws VcxException the vcx exception.
     *
     */
    public static CompletableFuture<Integer> issuerCredentialGetState(int credentialHandle) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        logger.debug("issuerCredentialGetState() called with: credentialHandle = [" + credentialHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int issue = addFuture(future);
        int result = LibVcx.api.vcx_issuer_credential_get_state(issue, credentialHandle, issuerCredentialGetStateCB);
        checkResult(result);
        return future;
    }
    private static Callback issuerSendCredentialCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            future.complete(err);
        }
    };

    /**
     * Sends the credential to the end user (holder).<br>
     *
     * @param credentialHandle Credential handle that was provided during creation. Used to identify credential object.
     * @param connectionHandle Connection handle that identifies pairwise connection.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer issuerCreateCredential example for credential demo"
     * @see #issuerCreateCredential
     */
    public static CompletableFuture<Integer> issuerSendCredential(int credentialHandle,
                                                                 int connectionHandle) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        ParamGuard.notNull(connectionHandle, "connectionHandle");
        logger.debug("issuerSendCredential() called with: credentialHandle = [" + credentialHandle + "], connectionHandle = [" + connectionHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int issue = addFuture(future);

        int result = LibVcx.api.vcx_issuer_send_credential(
                issue,
                credentialHandle,
                connectionHandle,
                issuerSendCredentialCB);

        checkResult(result);
        return future;
    }

    public static CompletableFuture<String> issuerGetCredentialMsg(int credentialHandle,
                                                                   String myPwDid) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        logger.debug("issuerGetCredentialMsg() called with: credentialHandle = [****]");
        CompletableFuture<String> future = new CompletableFuture<>();
        int issue = addFuture(future);

        int result = LibVcx.api.vcx_issuer_get_credential_msg(
                issue,
                credentialHandle,
                myPwDid,
                issuerCredentialStringCB
        );
        checkResult(result);
        return future;
    }

    private static Callback issuerCredentialStringCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, String stringData) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], string = [" + stringData + "]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            // TODO complete with exception if we find error
//            if (err != 0) {
//                future.completeExceptionally();
//            } else {
//
//            }
            String result = stringData;
            future.complete(result);
        }
    };

    /**
     * Takes the credential object and returns a json string of all its attributes<br>
     * Serializes the  issuer credential object for storage and later deserialization.
     *
     * @param credentialHandle Credential handle that was provided during creation. Used to identify credential object.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer issuerCreateCredential example for credential demo"
     * @see #issuerCreateCredential
     */
    public static CompletableFuture<String> issuerCredentialSerialize(int credentialHandle) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        logger.debug("issuerCredentialSerialize() called with: credentialHandle = [" + credentialHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<>();
        int issue = addFuture(future);

        int result = LibVcx.api.vcx_issuer_credential_serialize(
                issue,
                credentialHandle,
                issuerCredentialStringCB
        );
        checkResult(result);
        return future;
    }

    private static Callback issuerCredentialDeserializeCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, int handle) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], handle = [" + handle + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            // TODO complete with exception if we find error
//            if (err != 0) {
//                future.completeExceptionally();
//            } else {
//
//            }
            Integer result = handle;
            future.complete(result);
        }
    };

    public static CompletableFuture<Integer> issuerCredentialDeserialize(String serializedData) throws VcxException {
        ParamGuard.notNull(serializedData, "serializedData");
        logger.debug("issuerCredentialDeserialize() called with: serializedData = [****]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int issue = addFuture(future);

        int result = LibVcx.api.vcx_issuer_credential_deserialize(
                issue,
                serializedData,
                issuerCredentialDeserializeCB
        );
        checkResult(result);
        return future;
    }



    public static CompletableFuture<Integer> issuerTerminateCredential(
            int credentialHandle,
            int state,
            String msg
    ) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        ParamGuard.notNull(state, "state");
        ParamGuard.notNullOrWhiteSpace(msg, "msg");
        logger.debug("issuerTerminateCredential() called with: credentialHandle = [" + credentialHandle + "], state = [" + state + "], msg = [****]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int issue = addFuture(future);

        int result = LibVcx.api.vcx_issuer_terminate_credential(
                issue,
                credentialHandle,
                state,
                msg);
        checkResult(result);

        return future;

    }
    /**
     * Releases the issuer credential object by deallocating memory
     *
     * @param credentialHandle Credential handle that was provided during creation. Used to identify credential object.
     * @return Success
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer issuerCreateCredential example for credential demo"
     * @see #issuerCreateCredential
     */
    public static int issuerCredentialRelease(int credentialHandle) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        logger.debug("issuerCredentialRelease() called with: credentialHandle = [" + credentialHandle + "]");

        int result = LibVcx.api.vcx_issuer_credential_release(credentialHandle);
        checkResult(result);

        return result;
    }
    /**
     * Issuer Get Credential Request
     *
     * @param credentialHandle Credential handle that was provided during creation. Used to identify credential object.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Integer> issuerCredentialRequest(
            int credentialHandle,
            String credentialRequest) throws VcxException {

        ParamGuard.notNull(credentialHandle, "credentialHandle");
        ParamGuard.notNull(credentialRequest, "credentialRequest");
        logger.debug("issuercredentialRequest() called with: credentialHandle = [" + credentialHandle + "], credentialRequest = [****]");
        CompletableFuture<Integer> future = new CompletableFuture<>();

        int result = LibVcx.api.vcx_issuer_get_credential_request(
                credentialHandle,
                credentialRequest);
        checkResult(result);

        return future;
    }
    /**
     * Issuer Accept Credential Request
     *
     * @param credentialHandle Credential handle that was provided during creation. Used to identify credential object.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Integer> issuerAcceptRequest(
            int credentialHandle) throws VcxException {

        ParamGuard.notNull(credentialHandle, "credentialHandle");
        logger.debug("issuerAcceptRequest() called with: credentialHandle = [" + credentialHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();

        int result = LibVcx.api.vcx_issuer_accept_credential(
                credentialHandle);
        checkResult(result);

        return future;
    }

    private static Callback issuerRevokeCredentialCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            future.complete(err);
        }
    };

    /**
     * Revoke Credential<br>
     *
     * @param credentialHandle Credential handle that was provided during creation. Used to identify credential object.
     * @return completable future
     * @throws VcxException the vcx exception.
     * <pre><span style="color: gray;font-style: italic;">
     *   Example:
     *
     *   // Get Credential
     *         String threadId = JsonPath.read(payloadMessage,"$.~thread.thid");
     *         String credentialRecord = WalletApi.{@link com.evernym.sdk.vcx.wallet.WalletApi#getRecordWallet getRecordWallet}("credential", threadId, "").get();
     *         String serializedCredential = JsonPath.read(credentialRecord,"$.value");
     *         int credentialHandle = IssuerApi.{@link #issuerCredentialDeserialize issuerCredentialDeserialize}(serializedCredential).get();
     *         int credentialState = IssuerApi.{@link #issuerCredentialUpdateState issuerCredentialUpdateState}(credentialHandle).get();
     *   // Revoke Credential
     *         if (credentialState == VcxState.Accepted.getValue()) {
     *             IssuerApi.{@link #issuerRevokeCredential issuerRevokeCredential}(credentialHandle);
     *
     *             serializedCredential = IssuerApi.{@link #issuerCredentialSerialize issuerCredentialSerialize}(credentialHandle).get();
     *   // updateRecordWallet
     *             WalletApi.{@link com.evernym.sdk.vcx.wallet.WalletApi#updateRecordWallet updateRecordWallet}("credential", threadId, serializedCredential).get();
     *         }
     *         else {
     *             log.severe("Unexpected state type");
     *         }
     *   // Release
     *         IssuerApi.{@link #issuerCredentialRelease issuerCredentialRelease}(credentialHandle);
     * </span></pre>
     * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/53bda51f7fff5d5379faa680fac10d96253b1302/src/main/java/webhook/faber/GlobalService.java" target="_blank">VCX JAVA Demo - Credential Revocation Example</a>
     *
     */
    public static CompletableFuture<Integer> issuerRevokeCredential(int credentialHandle) throws VcxException {
        ParamGuard.notNull(credentialHandle, "credentialHandle");
        logger.debug("issuerRevokeCredential() called with: credentialHandle = [" + credentialHandle + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int issue = addFuture(future);

        int result = LibVcx.api.vcx_issuer_revoke_credential(
                issue,
                credentialHandle,
                issuerRevokeCredentialCB);

        checkResult(result);
        return future;
    }
}
