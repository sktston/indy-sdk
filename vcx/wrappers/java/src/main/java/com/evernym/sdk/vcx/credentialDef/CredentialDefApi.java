package com.evernym.sdk.vcx.credentialDef;

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
public class CredentialDefApi extends VcxJava.API {

    private static final Logger logger = LoggerFactory.getLogger("CredentialDefApi");
    private static Callback credentialDefCreateCB = new Callback() {
        // TODO: This callback and jna definition needs to be fixed for this API
        // it should accept connection handle as well
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, int credentialDefHandle) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], credentialDefHandle = [" + credentialDefHandle + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            Integer result = credentialDefHandle;
            future.complete(result);
        }
    };


    /**
     * Create a new CredentialDef object and publish correspondent record on the ledger.<br>
     *
     * @param sourceId Enterprise's personal identification for the user.
     * @param credentialName Name of credential definition
     * @param schemaId The schema id given during the creation of the schema
     * @param issuerId did corresponding to entity issuing a credential. Needs to have Trust Anchor permissions on ledger
     * @param tag way to create a unique credential def with the same schema and issuer did.
     * @param config type-specific configuration of credential definition revocation
     * <pre><span style="color: gray;font-style: italic;"> Example:
     *   support_revocation: true|false - Optional, by default its false
     *   tails_file: path to tails file - Optional if support_revocation is false
     *   max_creds: size of tails file - Optional if support_revocation is false </span></pre>
     * @param paymentHandle future use (currently uses any address in wallet)
     * @return completable future
     * @throws VcxException the vcx exception.
     *
     */
    public static CompletableFuture<Integer> credentialDefCreate(String sourceId,
                                                                 String credentialName,
                                                                 String schemaId,
                                                                 String issuerId,
                                                                 String tag,
                                                                 String config,
                                                                 int paymentHandle
    ) throws VcxException {
        ParamGuard.notNullOrWhiteSpace(sourceId, "sourceId");
        ParamGuard.notNullOrWhiteSpace(credentialName, "credentialName");
        ParamGuard.notNullOrWhiteSpace(schemaId, "schemaId");
        logger.debug("credentialDefCreate() called with: sourceId = [" + sourceId + "], credentialName = [" + credentialName + "], schemaId = [" + schemaId + "], issuerId = [****], tag = [" + tag + "], config = [" + config + "], paymentHandle = [" + paymentHandle + "]");
        //TODO: Check for more mandatory params in vcx to add in PamaGuard
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_credentialdef_create(
                commandHandle,
                sourceId,
                credentialName,
                schemaId,
                issuerId,
                tag,
                config,
                paymentHandle,
                credentialDefCreateCB
        );
        checkResult(result);
        return future;
    }

    private static Callback credentialDefSerializeCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, String serializedData) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], serializedData = [****]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            // TODO complete with exception if we find error
//            if (err != 0) {
//                future.completeExceptionally();
//            } else {
//
//            }
            String result = serializedData;
            future.complete(result);
        }
    };

    /**
     * Takes the credentialdef object and returns a json string of all its attributes. <br>
     * Serialize the object for storage.
     *
     * @param credentialDefHandle Credentialdef handle that was provided during creation. Used to access credentialdef object.
     * @return completable future
     * @throws VcxException the vcx exception.
     *
     */
    public static CompletableFuture<String> credentialDefSerialize(int credentialDefHandle) throws VcxException {
        ParamGuard.notNull(credentialDefHandle, "credentialDefHandle");
        logger.debug("credentialDefSerialize() called with: credentialDefHandle = [" + credentialDefHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_credentialdef_serialize(
                commandHandle,
                credentialDefHandle,
                credentialDefSerializeCB
        );
        checkResult(result);
        return future;
    }

    private static Callback credentialDefDeserialize = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, int credntialDefHandle) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], credntialDefHandle = [" + credntialDefHandle + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            // TODO complete with exception if we find error
//            if (err != 0) {
//                future.completeExceptionally();
//            } else {
//
//            }
            Integer result = credntialDefHandle;
            future.complete(result);
        }
    };

    /**
     * Takes a json string representing a credentialdef object and recreates an object matching the json. <br>
     * Create the object from a previously serialized object.
     *
     * @param credentialDefData Credentialdef handle that was provided during creation. Used to access credentialdef object.
     * @return completable future
     * @throws VcxException the vcx exception.
     *
     */
    public static CompletableFuture<Integer> credentialDefDeserialize(String credentialDefData) throws VcxException {
        ParamGuard.notNull(credentialDefData, "credentialDefData");
        logger.debug("credentialDefSerialize() called with: credentialDefData = [****]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_credentialdef_deserialize(
                commandHandle,
                credentialDefData,
                credentialDefDeserialize
        );
        checkResult(result);
        return future;
    }


    private static Callback credentialDefGetCredentialDefIdCb = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, String credentialDefId) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], credentialDefId = [" + credentialDefId + "]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            future.complete(credentialDefId);
        }
    };

    /**
     * Retrieves credential definition's id. <br>
     * Get the ledger ID of the object.
     *
     * @param credDefHandle CredDef handle that was provided during creation. Used to access proof object.
     * @return completable future
     * @throws VcxException the vcx exception.
     *
     */
    public static CompletableFuture<String> credentialDefGetCredentialDefId(int credDefHandle) throws VcxException {
        ParamGuard.notNull(credDefHandle, "credDefHandle");
        logger.debug("credentialDefGetCredentialDefId() called with: credDefHandle = [" + credDefHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);
        int result = LibVcx.api.vcx_credentialdef_get_cred_def_id(commandHandle,credDefHandle, credentialDefGetCredentialDefIdCb);
        checkResult(result);
        return future;
    }
    /**
     * Releases the credentialdef object by de-allocating memory. <br>
     * destroy the object and release any memory associated with it.
     *
     * @param handle Proof handle that was provided during creation. Used to access credential object.
     * @return Success
     * @throws VcxException the vcx exception.
     *
     */
    public static int credentialDefRelease(int handle) throws VcxException {
        ParamGuard.notNull(handle, "handle");
        logger.debug("credentialDefRelease() called with: handle = [" + handle + "]");

        int result = LibVcx.api.vcx_credentialdef_release(handle);
        checkResult(result);

        return result;
    }

    private static Callback credentialDefPrepareForEndorserCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int command_handle, int err, int handle, String credentialDefTxn, String revocRegDefTxn, String revocRegEntryTxn) {
	        System.out.println("callback() called with: command_handle = [" + command_handle + "], err = [" + err + "], handle = [" + handle + "], credentialDefTxn = [" + credentialDefTxn + "], revocRegDefTxn = [" + revocRegDefTxn + "], revocRegEntryTxn = [" + revocRegEntryTxn + "]");
            CompletableFuture<CredentialDefPrepareForEndorserResult> future = (CompletableFuture<CredentialDefPrepareForEndorserResult>) removeFuture(command_handle);
            if (!checkCallback(future, err)) return;
	        CredentialDefPrepareForEndorserResult result = new CredentialDefPrepareForEndorserResult(handle, credentialDefTxn, revocRegDefTxn, revocRegEntryTxn);
            future.complete(result);
        }
    };

    /**
     * Create a new CredentialDef object that will be published by Endorser later.<br>
     * Note that CredentialDef can't be used for credential issuing until it will be published on the ledger.<br>
     *
     * @param sourceId Enterprise's personal identification for the user.
     * @param credentialName Name of credential definition
     * @param schemaId The schema id given during the creation of the schema
     * @param issuerId did corresponding to entity issuing a credential. Needs to have Trust Anchor permissions on ledger
     * @param tag way to create a unique credential def with the same schema and issuer did.
     * @param config type-specific configuration of credential definition revocation
     * <pre><span style="color: gray;font-style: italic;"> Example:
     *   support_revocation: true|false - Optional, by default its false
     *   tails_file: path to tails file - Optional if support_revocation is false
     *   max_creds: size of tails file - Optional if support_revocation is false </span></pre>
     * @param endorser DID of the Endorser that will submit the transaction.
     * @return A created credential
     * @throws VcxException the vcx exception.
     * <pre><span style="color: gray;font-style: italic;">
     *   Example:
     *   // get schema Info
     *         String schemaRecord = WalletApi.{@link com.evernym.sdk.vcx.wallet.WalletApi#getRecordWallet getRecordWallet}("schema", "defaultSchema", "").get();
     *         String schema = JsonPath.read(schemaRecord, "$.value");
     *         String schemaId = JsonPath.read(schema, "$.data.schema_id");
     *         String version = JsonPath.read(schema, "$.data.version"); // not need same with schema version
     *   // get Issuer
     *         String vcxConfigRecord = WalletApi.{@link com.evernym.sdk.vcx.wallet.WalletApi#getRecordWallet getRecordWallet}("vcxConfig", "defaultVcxConfig", "").get();
     *         String vcxConfig = JsonPath.read(vcxConfigRecord, "$.value");
     *         String faberDid = JsonPath.read(vcxConfig, "$.institution_did");
     *   // define credential definition with actually needed
     *         String credDefData = JsonPath.parse("{" +
     *                 "  schemaId: '" + schemaId + "'," +
     *                 "  tag: 'tag." + version + "'," +
     *                 "  config: {" +
     *                 "    support_revocation: true," +
     *                 "    tails_file: '" + tailsFileRoot + "'," + // tails file is created here when credentialDefPrepareForEndorser
     *                 "    max_creds: 10" +
     *                 "  }" +
     *                 "}").jsonString();
     *   // Create a new credential definition object
     *         CredentialDefPrepareForEndorserResult credDefObject = CredentialDefApi.{@link #credentialDefPrepareForEndorser credentialDefPrepareForEndorser}("'cred_def_uuid'",
     *                 "cred_def_name",
     *                 JsonPath.read(credDefData, "$.schemaId"),
     *                 null,
     *                 JsonPath.read(credDefData, "$.tag"),
     *                 JsonPath.parse((LinkedHashMap)JsonPath.read(credDefData,"$.config")).jsonString(),
     *                 faberDid).get();
     * </span></pre>
     * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/53bda51f7fff5d5379faa680fac10d96253b1302/src/main/java/webhook/faber/GlobalService.java" target="_blank">VCX JAVA Demo - Credential Definition Create Example</a>
     *
     */
	public static CompletableFuture<CredentialDefPrepareForEndorserResult> credentialDefPrepareForEndorser(String sourceId,
	                                                                                                       String credentialName,
	                                                                                                       String schemaId,
	                                                                                                       String issuerId,
	                                                                                                       String tag,
	                                                                                                       String config,
	                                                                                                       String endorser
	) throws VcxException {
		ParamGuard.notNullOrWhiteSpace(sourceId, "sourceId");
		ParamGuard.notNull(credentialName, "credentialName");
		ParamGuard.notNull(schemaId, "schemaId");
		ParamGuard.notNull(endorser, "endorser");
		logger.debug("credentialDefCreate() called with: sourceId = [" + sourceId + "], credentialName = [" + credentialName + "], schemaId = [" + schemaId + "], issuerId = [" + issuerId + "], tag = [" + tag + "], config = [" + config + "], endorser = [" + endorser + "]");
		CompletableFuture<CredentialDefPrepareForEndorserResult> future = new CompletableFuture<CredentialDefPrepareForEndorserResult>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_credentialdef_prepare_for_endorser(
				commandHandle,
				sourceId,
				credentialName,
				schemaId,
				issuerId,
				tag,
				config,
				endorser,
				credentialDefPrepareForEndorserCB);
		checkResult(result);

		return future;
	}

	private static Callback vcxIntegerCB = new Callback() {
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
     * Checks if credential definition is published on the Ledger and updates the state if it is.<br>
     *
     * @param handle Credentialdef handle that was provided during creation. Used to access credentialdef object.
     * @return provides most current state of the credential definition and error status of request
     * <pre><span style="color: gray;font-style: italic;">Possible states:
     *              0 = Built
     *              1 = Published </span></pre>
     * @throws VcxException the vcx exception.
     * <pre><span style="color: gray;font-style: italic;">
     *   Example:
     *   // Create a new credential definition object
     *         CredentialDefPrepareForEndorserResult credDefObject = CredentialDefApi.{@link #credentialDefPrepareForEndorser credentialDefPrepareForEndorser}("'cred_def_uuid'",
     *                 "cred_def_name",
     *                 JsonPath.read(credDefData, "$.schemaId"),
     *                 null,
     *                 JsonPath.read(credDefData, "$.tag"),
     *                 JsonPath.parse((LinkedHashMap)JsonPath.read(credDefData,"$.config")).jsonString(),
     *                 faberDid).get();
     *         int credDefHandle = credDefObject.getCredentialDefHandle();
     *         String credDefTrx = credDefObject.getCredDefTransaction();
     *         String revRegDefTrx = credDefObject.getRevocRegDefTransaction();
     *         String revRegId = JsonPath.read(revRegDefTrx, "$.operation.id");
     *         String tailsFileHash = JsonPath.read(revRegDefTrx, "$.operation.value.tailsHash");
     *         String revRegEntryTrx = credDefObject.getRevocRegEntryTransaction();
     *
     *   // Publish credential definition and revocation registry on the ledger
     *         UtilsApi.{@link com.evernym.sdk.vcx.utils.UtilsApi#vcxEndorseTransaction vcxEndorseTransaction}(credDefTrx).get();
     *         revRegDefTrx = JsonPath.parse(revRegDefTrx).set("$.operation.value.tailsLocation", tailsServerUrl + "/" + revRegId).jsonString();
     *         UtilsApi.{@link com.evernym.sdk.vcx.utils.UtilsApi#vcxEndorseTransaction vcxEndorseTransaction}(revRegDefTrx).get();
     *         UtilsApi.{@link com.evernym.sdk.vcx.utils.UtilsApi#vcxEndorseTransaction vcxEndorseTransaction}(revRegEntryTrx).get();
     *         int credentialDefState = CredentialDefApi.{@link #credentialDefUpdateState credentialDefUpdateState}(credDefHandle).get();
     *         if (credentialDefState == 1)
     *             log.info("Published successfully");
     *         else
     *             log.warning("Publishing is failed");
     * </span></pre>
     * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/53bda51f7fff5d5379faa680fac10d96253b1302/src/main/java/webhook/faber/GlobalService.java" target="_blank">VCX JAVA Demo - Credential Definition Create Example</a>
     *
     */
	public static CompletableFuture<Integer> credentialDefUpdateState(int handle) throws VcxException {
		logger.debug("vcxSchemaUpdateState() called with: handle = [" + handle + "]");
		CompletableFuture<Integer> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_credentialdef_update_state(
				commandHandle,
				handle,
				vcxIntegerCB
		);
		checkResult(result);
		return future;
	}

    /**
     * Get the current state of the credential definition object. <br>
     *
     * @param handle Credentialdef handle that was provided during creation. Used to access credentialdef object.
     * @return provides most current state of the credential definition and error status of request
     * <pre><span style="color: gray;font-style: italic;">Possible states:
     *              0 = Built
     *              1 = Published </span></pre>
     * @throws VcxException the vcx exception.
     *
     */
	public static CompletableFuture<Integer> credentialDefGetState(int handle) throws VcxException {
		logger.debug("schemaGetState() called with: handle = [" + handle + "]");
		CompletableFuture<Integer> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_credentialdef_get_state(
				commandHandle,
				handle,
				vcxIntegerCB
		);
		checkResult(result);
		return future;
	}
}
