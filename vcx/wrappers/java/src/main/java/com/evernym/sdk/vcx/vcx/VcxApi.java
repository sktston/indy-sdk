package com.evernym.sdk.vcx.vcx;

import com.evernym.sdk.vcx.LibVcx;
import com.evernym.sdk.vcx.ParamGuard;
import com.evernym.sdk.vcx.VcxException;
import com.evernym.sdk.vcx.VcxJava;
import com.sun.jna.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;


/**
 * <h1>VCX Initialize and Common API</h1>
 * Initializes VCX APIs
 *
 * <p>
 * <b>Note:</b> written by SKT (The original is vcx and python wrapper documents)
 *
 * @author  JJ
 * @version 1.0
 * @since   2020-06-31
 */

public class VcxApi extends VcxJava.API {
    /**

     */
    private static final Logger logger = LoggerFactory.getLogger("VcxApi");

    private VcxApi() {
    }

    private static Callback vcxIniWithConfigCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            Integer result = err;
            future.complete(result);
        }
    };

    private static Callback vcxInitCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int xcommandHandle, int err) {
            logger.debug("callback() called with: xcommandHandle = [" + xcommandHandle + "], err = [" + err + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(xcommandHandle);
            if (!checkCallback(future, err)) return;
            int result = err;
            future.complete(result);

        }
    };

    /**
     * <p>VCX Init With Config.</p><br>
     * Initializes VCX with config settings
     * <br> An example file is at libvcx/sample_config/config.json
     * <pre><font color=gray>{@code
     * {
     *   "agency_did": "VsKV7grR1BUE29mG2Fm2kX",
     *   "agency_verkey": "Hezce2UWMZ3wUhVkh2LfKSs8nDzWwzs2Win7EzNN3YaR",
     *   "agency_endpoint": "http://localhost:8080",
     *   "genesis_path":"/var/lib/indy/verity-staging/pool_transactions_genesis",
     *   "institution_name": "institution",
     *   "institution_logo_url": "http://robohash.org/234",
     *   "institution_did": "EwsFhWVoc3Fwqzrwe998aQ",
     *   "institution_verkey": "8brs38hPDkw5yhtzyk2tz7zkp8ijTyWnER165zDQbpK6",
     *   "remote_to_sdk_did": "EtfeMFytvYTKnWwqTScp9D", // My Agent DID
     *   "remote_to_sdk_verkey": "8a7hZDyJK1nNCizRCKMr4H4QbDm8Gg2vcbDRab8SVfsi", // My Agent Verkey
     *   "sdk_to_remote_did": "KacwZ2ndG6396KXJ9NDDw6", // My pariwise DID for agent
     *   "sdk_to_remote_verkey": "B8LgZGxEPcpTJfZkeqXuKNLihM1Awm8yidqsNwYi5QGc", My pairwise Verkey for agent
     *   "payment_method": "null"
     * }
     * }</font></pre>
     * <br>The list of available options see here :
     * <a href="https://github.com/hyperledger/indy-sdk/blob/ebdf1b62b4b744b94155cb6f032367540b33556c/docs/configuration.md#configuration-options">
     *     Configuration options</a><br>
     *
     * @param configJson config as json
     * <br> Example:
     *   <pre>
     *   {@code
     *      VcxApi.vcxInitWithConfig(vcxConfig).get();
     *   }</pre>
     * @return
     * @see <a href="https://github.com/hyperledger/indy-sdk/blob/ebdf1b62b4b744b94155cb6f032367540b33556c/docs/configuration.md">Indy SDK configuration</a>
     * @since 1.0
     */
    public static CompletableFuture<Integer> vcxInitWithConfig(String configJson) throws VcxException {
        ParamGuard.notNullOrWhiteSpace(configJson, "config");
        logger.debug("vcxInitWithConfig() called with: configJson = [****]");
        CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_init_with_config(
                commandHandle,
                configJson,
                vcxIniWithConfigCB);
        checkResult(result);

        return future;

    }
    /**
     * <p>Initializes VCX with config file.</P><br>
     * An example file is at libvcx/sample_config/config.json
     * <pre><font color=gray>{@code
     * {
     *   "agency_did": "VsKV7grR1BUE29mG2Fm2kX",
     *   "agency_verkey": "Hezce2UWMZ3wUhVkh2LfKSs8nDzWwzs2Win7EzNN3YaR",
     *   "agency_endpoint": "http://localhost:8080",
     *   "genesis_path":"/var/lib/indy/verity-staging/pool_transactions_genesis",
     *   "institution_name": "institution",
     *   "institution_logo_url": "http://robohash.org/234",
     *   "institution_did": "EwsFhWVoc3Fwqzrwe998aQ",
     *   "institution_verkey": "8brs38hPDkw5yhtzyk2tz7zkp8ijTyWnER165zDQbpK6",
     *   "remote_to_sdk_did": "EtfeMFytvYTKnWwqTScp9D", // My Agent DID
     *   "remote_to_sdk_verkey": "8a7hZDyJK1nNCizRCKMr4H4QbDm8Gg2vcbDRab8SVfsi", // My Agent Verkey
     *   "sdk_to_remote_did": "KacwZ2ndG6396KXJ9NDDw6", // My pariwise DID for agent
     *   "sdk_to_remote_verkey": "B8LgZGxEPcpTJfZkeqXuKNLihM1Awm8yidqsNwYi5QGc", My pairwise Verkey for agent
     *   "payment_method": "null"
     * }
     * }</font></pre>
     * The list of available options see here :
     * <a href="https://github.com/hyperledger/indy-sdk/blob/ebdf1b62b4b744b94155cb6f032367540b33556c/docs/configuration.md#configuration-options">
     *     Configuration options</a><br>
     *
     * @param configPath path to a config file to populate config attributes<br>
     * Example:
     *   <pre>
     *   {@code
     *   VcxApi.vcxInit('/home/username/config.json').get();
     *   }</pre>
     * @return
     * @see <a href="https://github.com/hyperledger/indy-sdk/blob/ebdf1b62b4b744b94155cb6f032367540b33556c/docs/configuration.md">Indy SDK configuration</a>
     * @since 1.0
     */
    public static CompletableFuture<Integer> vcxInit(String configPath) throws VcxException {
        ParamGuard.notNullOrWhiteSpace(configPath, "configPath");
        logger.debug("vcxInit() called with: configPath = [" + configPath + "]");
        CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_init(
                commandHandle, configPath,
                vcxInitCB);
        checkResult(result);
        return future;
    }
    /**
     * <p>Initializes VCX with minimal (no-agency) config file AFTER the wallet and pool are set.</P><br>
     * <br>Initialize vcx with the minimal configuration (wallet, pool must already be set with
     * <br>vcx wallet set and vcx pool set and without any agency configuration
     * An example file
     * <pre><font color=gray>{@code
     * {
     *   "institution_name": "institution",
     *   "institution_logo_url": "http://robohash.org/234",
     *   "institution_did": "EwsFhWVoc3Fwqzrwe998aQ",
     *   "institution_verkey": "8brs38hPDkw5yhtzyk2tz7zkp8ijTyWnER165zDQbpK6",
     * }
     * }</font></pre>
     *
     * @param configJson minimal configuration<br>
     * Example:
     *   <pre>
     *   {@code
     *      WalletApi.setWalletHandle(1);
     *      UtilsApi.setPoolHandle(1);
     *      assert (VcxApi.vcxInitMinimal("{\"institution_name\":\"f\",\"institution_did\":\"4\", \"institution_verkey\":\"4\"}") == 0);
     *   }</pre>
     * @return
     * @since 1.0
     */
    public static int vcxInitMinimal(String configJson) throws VcxException {
        ParamGuard.notNullOrWhiteSpace(configJson, "config");
        logger.debug("vcxInitMinimal() called with: configJson = [" + configJson + "]");

        int result = LibVcx.api.vcx_init_minimal(
                configJson);
        checkResult(result);

        return result;
    }


    /**
     * <p>VCX Shutdown.</P><br>
     * <br> Reset libvcx to a pre-configured state, releasing/deleting any handles and freeing memory
     * <br> libvcx will be inoperable and must be initialized again with vcx_init_with_config
     *
     * @param deleteWallet specify whether wallet/pool should be deleted
     * <br>Example:
     *   <pre>
     *   {@code
     *      VcxApi.vcxShutdown(false);
     *   }</pre>
     * @return Success/Fail
     * @since 1.0
     */
    public static int vcxShutdown(Boolean deleteWallet) throws VcxException {
        logger.debug("vcxShutdown() called with: deleteWallet = [" + deleteWallet + "]");
        int result = LibVcx.api.vcx_shutdown(deleteWallet);
        checkResult(result);
        return result;
    }

    public static String vcxVersion() throws VcxException {
        logger.debug("vcxVersion()");
        return LibVcx.api.vcx_version();
    }

    /**
     * <p>VCX Error C Message</P><br>
     * <br> Get the message corresponding to an error code
     *
     * @param errorCode code of error
     * <br>Example:
     *   <pre>
     *   {@code
     *      String errorCMessage = VcxApi.vcxErrorCMessage(1001);
     *   }</pre>
     * @return Error message
     * @since 1.0
     */
    public static String vcxErrorCMessage(int errorCode) {
        logger.debug("vcxErrorCMessage() called with: errorCode = [" + errorCode + "]");
        return LibVcx.api.vcx_error_c_message(errorCode);

    }

    /**
     * <p>VCX Error C Message.</P><br>
     * <br> Get the message corresponding to an error code
     *
     * @param name institution name
     * @param logoUrl url containing institution logo
     * <br>Example:
     *   <pre>
     *   {@code
     *      String errorCMessage = VcxApi.vcxErrorCMessage(1001);
     *   }</pre>
     * @return Error message
     * @since 1.0
     */
    public static int vcxUpdateInstitutionInfo(String name, String logoUrl) throws VcxException {
        ParamGuard.notNullOrWhiteSpace(name, "name");
        ParamGuard.notNullOrWhiteSpace(logoUrl, "logoUrl");
        logger.debug("vcxUpdateInstitutionInfo() called with: name = [" + name + "], logoUrl = [" + logoUrl + "]");

        int result = LibVcx.api.vcx_update_institution_info(
                name,
                logoUrl);
        checkResult(result);

        return result;
    }

    private static Callback vcxUpdateWebhookUrlCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            future.complete(err);
        }
    };

    /**
     * <p>VCX Webhook URL Update. </P><br>
     * <br> Update Webhook URL for notification in Agent
     *
     * @param notificationWebhookUrl institution webhook URL
     * <br>Example:
     *   <pre>
     *   {@code
     *      VcxApi.vcxUpdateWebhookUrl('http://localhost:7201/notifications').get();
     *   }</pre>
     * @return
     * @since 1.0
     */
    public static CompletableFuture<Integer> vcxUpdateWebhookUrl(String notificationWebhookUrl) throws VcxException {
        ParamGuard.notNullOrWhiteSpace(notificationWebhookUrl, "notificationWebhookUrl");
        logger.debug("vcxUpdateWebhookUrl() called with: notificationWebhookUrl = [" + notificationWebhookUrl + "]");
        CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_update_webhook_url(
                commandHandle,
                notificationWebhookUrl,
                vcxUpdateWebhookUrlCB);
        checkResult(result);

        return future;
    }
}