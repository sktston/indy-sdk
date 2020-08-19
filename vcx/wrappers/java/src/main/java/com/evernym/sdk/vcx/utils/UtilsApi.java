package com.evernym.sdk.vcx.utils;

import com.evernym.sdk.vcx.LibVcx;
import com.evernym.sdk.vcx.ParamGuard;
import com.evernym.sdk.vcx.VcxException;
import com.evernym.sdk.vcx.VcxJava;
import com.sun.jna.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * <h1>VCX Schema API.</h1>
 * VCX Schema APIs
 *
 * Created by abdussami on 17/05/18.<br>
 * Javadoc as written by JJ (Referring to libvcx and python wrapper documents)
 *
 * @version 1.1
 * @since   11/08/2020
 */
public class UtilsApi extends VcxJava.API {
    private static final Logger logger = LoggerFactory.getLogger("UtilsApi");
    private static Callback provAsyncCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, String config) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], config = [" + config + "]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;

            String result = config;
            future.complete(result);
        }
    };
    /**
     * Provision an agent in the agency, populate configuration and wallet for this agent.<br>
     * NOTE: for asynchronous call use vcx_agent_provision_async
     *
     * @param config configuration
     * @return result
     * <pre><span style="color: gray;font-style: italic;">
     *  Example:
     *  // VCX Agent Provision Config
     *          String provisionConfig  = JsonPath.parse("{" +
     *                 "  agency_url: 'http://localhost:8080'," + // use local
     *                 "  agency_did: 'VsKV7grR1BUE29mG2Fm2kX'," +
     *                 "  agency_verkey: 'Hezce2UWMZ3wUhVkh2LfKSs8nDzWwzs2Win7EzNN3YaR'," +
     *                 "  wallet_name: 'node_vcx_demo_faber_wallet_" + utime + "'," +
     *                 "  wallet_key: '123'," +
     *                 "  payment_method: 'null'," +
     *                 "  enterprise_seed: '00000000000000000000000Endorser1'" + // SEED of faber's DID already registered in the ledger
     *                 "}").jsonString();
     *
     *  // Communication method. aries.
     *  // Running with Aries VCX Enabled! Make sure VCX agency is configured to use protocol_type 4.0
     *         provisionConfig = JsonPath.parse(provisionConfig).put("$", "protocol_type", "4.0").jsonString();
     *
     *  // add webhook url to config
     *         provisionConfig = JsonPath.parse(provisionConfig).put("$", "webhook_url", webhookUrl).jsonString();
     *
     *  // Config used to provision agent in agency
     *         String vcxConfig = UtilsApi.{@link #vcxProvisionAgent vcxProvisionAgent}(provisionConfig);
     *  // VCX Config & provision to initialize VCX
     *         vcxConfig = JsonPath.parse(vcxConfig).put("$", "institution_name", "faber")
     *                 .put("$", "institution_logo_url", "http://robohash.org/234")
     *                 .put("$", "protocol_version", "2")
     *                 .put("$", "genesis_path", System.getProperty("user.dir") + "/genesis.txn").jsonString();
     *         VcxApi.{@link com.evernym.sdk.vcx.vcx.VcxApi#vcxInitWithConfig vcxInitWithConfig}(vcxConfig).get();
     *
     *  // addRecordWallet
     *         WalletApi.{@link com.evernym.sdk.vcx.wallet.WalletApi#addRecordWallet addRecordWallet}("vcxConfig", "defaultVcxConfig", vcxConfig, "").get();

     * </span></pre>
     * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/master/src/main/java/webhook/faber/GlobalService.java" target="_blank">VCX JAVA Demo - Schema Create Example</a>
     * @since 1.0
     */
    public static String vcxProvisionAgent(String config) {
        ParamGuard.notNullOrWhiteSpace(config, "config");
        logger.debug("vcxProvisionAgent() called with: config = [****]");
        String result = LibVcx.api.vcx_provision_agent(config);

        return result;

    }
    /**
     * asynchronous provision an agent in the agency, populate configuration and wallet for this agent.<br>
     *
     * @param conf configuration
     * @return result
     * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/master/src/main/java/webhook/faber/GlobalService.java" target="_blank">VCX JAVA Demo - Schema Create Example</a>
     * @since 1.0
     */
    public static CompletableFuture<String> vcxAgentProvisionAsync(String conf) throws VcxException {
        CompletableFuture<String> future = new CompletableFuture<String>();
        logger.debug("vcxAgentProvisionAsync() called with: conf = [****]");
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_agent_provision_async(
                commandHandle, conf,
                provAsyncCB);
        checkResult(result);
        return future;
    }

    private static Callback vcxUpdateAgentInfoCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            Integer result = commandHandle;
            future.complete(result);
        }
    };
    /**
     * Update information on the agent (ie, comm method and type)<br>
     *
     * @param config updated configuration
     * <br>
     * @return completable future
     * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/master/src/main/java/webhook/faber/GlobalService.java" target="_blank">VCX JAVA Demo - Schema Create Example</a>
     * @since 1.0
     */
    public static CompletableFuture<Integer> vcxUpdateAgentInfo(String config) throws VcxException {
        ParamGuard.notNullOrWhiteSpace(config, "config");
        logger.debug("vcxUpdateAgentInfo() called with: config = [****]");
        CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_agent_update_info(
                commandHandle,
                config,
                vcxUpdateAgentInfoCB
        );
        checkResult(result);
        return future;
    }

    private static Callback vcxGetMessagesCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, String messages) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], messages = [****]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            String result = messages;
            future.complete(result);
        }
    };
    /**
     * Retrieve messages from the agent
     *
     * @param messageStatus target message status
     * <pre><span style="color: gray;font-style: italic;"> Statuses:
     *  MS-101 - Created
     *  MS-102 - Sent
     *  MS-103 - Received
     *  MS-104 - Accepted
     *  MS-105 - Rejected
     *  MS-106 - Reviewed</span></pre>
     * @param uids optional, comma separated - query for messages with the specified uids
     * @param pwdids optional, comma separated - DID's pointing to specific connection
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer to credentialSendRequest example for vcx update message demo"
     * @see com.evernym.sdk.vcx.credential.CredentialApi#credentialSendRequest
     */
    public static CompletableFuture<String> vcxGetMessages(String messageStatus, String uids, String pwdids) throws VcxException {
        ParamGuard.notNullOrWhiteSpace(messageStatus, "messageStatus");
        logger.debug("vcxGetMessages() called with: messageStatus = [" + messageStatus + "], uids = [" + uids + "], pwdids = [****]");
        CompletableFuture<String> future = new CompletableFuture<String>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_messages_download(
                commandHandle,
                messageStatus,
                uids,
                pwdids,
                vcxGetMessagesCB
        );
        checkResult(result);
        return future;
    }
    /**
     * Retrieve messages from the Cloud Agent
     *
     * @param messageStatus optional - query for messages with the specified status
     * <pre><span style="color: gray;font-style: italic;"> Statuses:
     *  MS-101 - Created
     *  MS-102 - Sent
     *  MS-103 - Received
     *  MS-104 - Accepted
     *  MS-105 - Rejected
     *  MS-106 - Reviewed</span></pre>
     * @param uids optional, comma separated - query for messages with the specified uids
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<String> vcxGetAgentMessages(String messageStatus, String uids) throws VcxException {
        ParamGuard.notNullOrWhiteSpace(messageStatus, "messageStatus");
        logger.debug("vcxGetAgentMessages() called with: messageStatus = [" + messageStatus + "], uids = [" + uids + "]");
        CompletableFuture<String> future = new CompletableFuture<String>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_download_agent_messages(
                commandHandle,
                messageStatus,
                uids,
                vcxGetMessagesCB
        );
        checkResult(result);
        return future;
    }

    private static Callback vcxUpdateMessagesCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            Integer result = commandHandle;
            future.complete(result);
        }
    };


    /**
     * Retrieves pw_did from Connection object
     *
     *
     * @param messageStatus target message status
     * <pre><span style="color: gray;font-style: italic;"> Statuses:
     *  MS-101 - Created
     *  MS-102 - Sent
     *  MS-103 - Received
     *  MS-104 - Accepted
     *  MS-105 - Rejected
     *  MS-106 - Reviewed</span></pre>
     * @param msgJson messages to update: [{"pairwiseDID":"QSrw8hebcvQxiwBETmAaRs","uids":["mgrmngq"]},...]
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer to credentialSendRequest example for vcx update message demo"
     * @see com.evernym.sdk.vcx.credential.CredentialApi#credentialSendRequest
     */
    public static CompletableFuture<Integer> vcxUpdateMessages(String messageStatus, String msgJson) throws VcxException {
        ParamGuard.notNullOrWhiteSpace(messageStatus, "messageStatus");
        ParamGuard.notNull(msgJson, "msgJson");
        logger.debug("vcxUpdateMessages() called with: messageStatus = [" + messageStatus + "], msgJson = [****]");
        CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_messages_update_status(
                commandHandle,
                messageStatus,
                msgJson,
                vcxUpdateMessagesCB
        );
        checkResult(result);
        return future;
    }

    private static Callback stringCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, String fees) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], fees = [" + fees + "]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            String result = fees;
            future.complete(result);
        }
    };
    /**
     * Get ledger fees from the network
     *
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<String> getLedgerFees() throws VcxException {
        logger.debug("getLedgerFees() called");
        CompletableFuture<String> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_ledger_get_fees(
                commandHandle,
                stringCB
        );
        checkResult(result);
        return future;
    }
    /**
     * Retrieve author agreement and acceptance mechanisms set on the Ledger
     *
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<String> getLedgerAuthorAgreement() throws VcxException {
        logger.debug("getLedgerAuthorAgreement() called");
        CompletableFuture<String> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_get_ledger_author_agreement(
                commandHandle,
                stringCB
        );
        checkResult(result);
        return future;
    }
    /**
     * Retrieve author agreement and acceptance mechanisms set on the Ledger
     *
     * @param text (optional) raw data about TAA from ledger.
     *     <br> These parameters should be passed together.
     *     <br> These parameters are required if hash parameter is ommited.
     * @param version target message status
     * @param hash (optional) hash on text and version. This parameter is required if text and version parameters are ommited.
     * @param accMechType mechanism how user has accepted the TAA
     * @param timeOfAcceptance UTC timestamp when user has accepted the TAA
     *
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static void setActiveTxnAuthorAgreementMeta(String text, String version,
                                                         String hash, String accMechType, long timeOfAcceptance) throws VcxException {
        ParamGuard.notNull(accMechType, "accMechType");
        logger.debug("vcxProvisionAgent() called with: text = [" + text + "], version = [" + version + "]," +
                " hash = [" + hash + "], accMechType = [" + accMechType + "], timeOfAcceptance = [" + timeOfAcceptance + "]");
        int result = LibVcx.api.vcx_set_active_txn_author_agreement_meta(text, version, hash, accMechType, timeOfAcceptance);
        checkResult(result);
    }

    public static void vcxMockSetAgencyResponse(int messageIndex) {
        logger.debug("vcxMockSetAgencyResponse() called");
        LibVcx.api.vcx_set_next_agency_response(messageIndex);
    }
    /**
     * Set the pool handle before calling vcx_init_minimal
     *
     * @param handle pool handle that libvcx should use
     */
    public static void setPoolHandle(int handle) {
        LibVcx.api.vcx_pool_set_handle(handle);
    }

    private static Callback getReqPriceAsyncCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, long price) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], price = [" + price + "]");
            CompletableFuture<Long> future = (CompletableFuture<Long>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;

            long result = price;
            future.complete(result);
        }
    };
    /**
     * Gets minimal request price for performing an action in case the requester can perform this action.
     *
     * @param actionJson json
     * <pre><span style="color: gray;font-style: italic;">{
     *     "auth_type": ledger transaction alias or associated value,
     *     "auth_action": type of an action.,
     *     "field": transaction field,
     *     "old_value": (Optional) old value of a field, which can be changed to a new_value (mandatory for EDIT action),
     *     "new_value": (Optional) new value that can be used to fill the field,
     * }</span></pre>
     * @param requesterInfoJson indojaon
     * <pre><span style="color: gray;font-style: italic;">{(Optional) {
     *     "role": string - role of a user which can sign transaction.
     *     "count": string - count of users.
     *     "is_owner": bool - if user is an owner of transaction.
     * } otherwise context info will be used</span></pre>
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Long> vcxGetRequestPrice(String actionJson, String requesterInfoJson) throws VcxException {
        ParamGuard.notNull(actionJson, "actionJson");
        logger.debug("vcxGetRequestPrice() called with: actionJson = [" + actionJson + "], requesterInfoJson = [" + requesterInfoJson + "]");
        CompletableFuture<Long> future = new CompletableFuture<Long>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_get_request_price(
                commandHandle, actionJson, requesterInfoJson,
                getReqPriceAsyncCB);
        checkResult(result);
        return future;
    }

    private static Callback vcxEndorseTransactionCb = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            Integer result = commandHandle;
            future.complete(result);
        }
    };
    /**
     * Endorse transaction to the ledger preserving an original author
     *
     * @param transactionJson transaction to endorse
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     */
    public static CompletableFuture<Integer> vcxEndorseTransaction(String transactionJson) throws VcxException {
        ParamGuard.notNull(transactionJson, "transactionJson");
        logger.debug("vcxEndorseTransaction() called with: transactionJson = [" + transactionJson + "]");
        CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_endorse_transaction(
                commandHandle, transactionJson,
                vcxEndorseTransactionCb);
        checkResult(result);
        return future;
    }
}
