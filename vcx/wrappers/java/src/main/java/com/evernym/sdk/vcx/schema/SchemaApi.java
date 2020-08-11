package com.evernym.sdk.vcx.schema;


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
 * VCX Schema APIs <br>
 * Javadoc as written by JJ (Referring to libvcx and python wrapper documents)
 *
 * @version 1.1
 * @since   11/08/2020
 */
public class SchemaApi extends VcxJava.API {
    private static final Logger logger = LoggerFactory.getLogger("SchemaApi");
    private static Callback schemaCreateCB = new Callback() {
        // TODO: This callback and jna definition needs to be fixed for this API
        // it should accept connection handle as well
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, int schemaHandle) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], schemaHandle = [" + schemaHandle + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            Integer result = schemaHandle;
            future.complete(result);
        }
    };

    /**
     * <p>Create a new Schema object and publish correspondent record on the ledger.</p>
     *
     * @param sourceId Enterprise's personal identification for the user.
     * @param schemaName version of schema.
     * @param version version of schema.
     * @param data list of attributes that will make up the schema (the number of attributes should be less or equal than 125).
     * @param paymentHandle future use (currently uses any address in the wallet).
     * <br><pre>Example schema_data -> "["attr1", "attr2", "attr3"]".</pre>
     * <br>
     * @return  schema object, written to ledger.
     * <pre><span style="color: gray;font-style: italic;">
     *  Example:
     *  // Schema Create
     *         String schemaData = JsonPath.parse("{" +
     *                 "  schema_name: 'degree_schema'," +
     *                 "  schema_version: '" + version + "'," +
     *                 "  attributes: ['name', 'last_name', 'date', 'degree', 'age']" +
     *                 "}").jsonString();
     *         int schemaHandle = SchemaApi.{@link #schemaCreate schemaCreate}("schema_uuid", // Schema Create
     *                 JsonPath.read(schemaData, "$.schema_name"),
     *                 JsonPath.read(schemaData, "$.schema_version"),
     *                 JsonPath.parse((List)JsonPath.read(schemaData, "$.attributes")).jsonString(),
     *                 0).get();
     *  // Get Schema ID
     *         String schemaId = SchemaApi.{@link #schemaGetSchemaId schemaGetSchemaId}(schemaHandle).get();
     *  // Schema Serialize
     *         String schema = SchemaApi.{@link #schemaSerialize schemaSerialize}(schemaHandle).get();
     *  // Add Record Wallet
     *         WalletApi.{@link com.evernym.sdk.vcx.wallet.WalletApi#addRecordWallet addRecordWallet}("schema", "defaultSchema", schema).get();
     *  // Schema Release
     *         SchemaApi.{@link #schemaRelease schemaRelease}(schemaHandle);
     *   }
     * </span></pre>
     * @see <a href = "https://github.com/sktston/vcx-demo-java/blob/master/src/main/java/webhook/faber/GlobalService.java" target="_blank">VCX JAVA Demo - Schema Create Example</a>
     * @since 1.0
     */
    public static CompletableFuture<Integer> schemaCreate(String sourceId,
                                                          String schemaName,
                                                          String version,
                                                          String data,
                                                          int paymentHandle) throws VcxException {
        ParamGuard.notNullOrWhiteSpace(sourceId, "sourceId");
        ParamGuard.notNullOrWhiteSpace(schemaName, "schemaName");
        ParamGuard.notNullOrWhiteSpace(version, "version");
        ParamGuard.notNullOrWhiteSpace(data, "data");
        logger.debug("schemaCreate() called with: sourceId = [" + sourceId + "], schemaName = [" + schemaName + "], version = [" + version + "]" + " data = <" + data + ">" + " payment_handle = <" + paymentHandle + ">");
        CompletableFuture<Integer> future = new CompletableFuture<Integer>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_schema_create(
                commandHandle,
                sourceId,
                schemaName,
                version,
                data,
                paymentHandle,
                schemaCreateCB
        );
        checkResult(result);
        return future;
    }

    private static Callback schemaSerializeHandle = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, String serializedData) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], serializedData = [" + serializedData + "]");
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
     * <p>Serialize the object for storage.</p>
     * <br> Takes the schema object and returns a json string of all its attributes
     * <br> See {@link #schemaCreate(String, String, String, String, int)} example for reference.
     * @param schemaHandle Schema handle that was provided during creation. Used to access schema object.
     * <br>
     * @return  serialized object
     * <br><pre>
     *         Serialized Schema:
     *         {
     *          "version": "1.0",
     *          "data": {
     *              "data": [
     *                  "name",
     *                  "last_name",
     *                  "date",
     *                  "degree",
     *                  "age"
     *                  ],
     *              "version": "59.4.40",
     *              "schema_id": "Th7MpTaRZVRYnPiabds81Y:2:degree_schema:59.4.40",
     *              "name": "degree_schema",
     *              "source_id": "schema_uuid",
     *              "state": 1
     *              }
     *          }
     * </pre>
     * @see "Refer to schemaCreate example for credential demo"
     * @see #schemaCreate
     */
    public static CompletableFuture<String> schemaSerialize(int schemaHandle) throws VcxException {
        ParamGuard.notNull(schemaHandle, "schemaHandle");
        logger.debug("schemaSerialize() called with: schemaHandle = [" + schemaHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_schema_serialize(
                commandHandle,
                schemaHandle,
                schemaSerializeHandle
        );
        checkResult(result);
        return future;
    }

    private static Callback schemaDeserializeCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, int schemaHandle) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], schemaHandle = [" + schemaHandle + "]");
            CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            // TODO complete with exception if we find error
//            if (err != 0) {
//                future.completeExceptionally();
//            } else {
//
//            }
            Integer result = schemaHandle;
            future.complete(result);
        }
    };
    /**
     * <p> Create the object from a previously serialized object.</p>
     * <br> Takes a json string representing a schema object and recreates an object matching the json.
     * <br> Attributes are provided by a previous call to the serialize function.
     * <br> See {@link #schemaCreate(String, String, String, String, int)} example for reference.
     * @param schemaData json string representing a schema object.
     * <br>
     * @return  A re-instantiated object
     * <br><pre>
     *         Serialized Schema:
     *         {
     *          "version": "1.0",
     *          "data": {
     *              "data": [
     *                  "name",
     *                  "last_name",
     *                  "date",
     *                  "degree",
     *                  "age"
     *                  ],
     *              "version": "59.4.40",
     *              "schema_id": "Th7MpTaRZVRYnPiabds81Y:2:degree_schema:59.4.40",
     *              "name": "degree_schema",
     *              "source_id": "schema_uuid",
     *              "state": 1
     *              }
     *          }
     * </pre>
     * @since 1.0
     */
    public static CompletableFuture<Integer> schemaDeserialize(String schemaData) throws VcxException {
        ParamGuard.notNull(schemaData, "schemaData");
        logger.debug("schemaDeserialize() called with: schemaData = [" + schemaData + "]");
        CompletableFuture<Integer> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_schema_deserialize(
                commandHandle,
                schemaData,
                schemaDeserializeCB
        );
        checkResult(result);
        return future;
    }

    private static Callback schemaGetAttributesCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err,int schemaHandle, String schemaAttributes) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], schemaHandle = [" + schemaHandle +  "],  schemaAttributes = [" + schemaAttributes + "]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            future.complete(schemaAttributes);
        }
    };
    /**
     * <p> Retrieves all of the data associated with a schema on the ledger.</p>
     * <br>
     * @param sourceId Enterprise's personal identification for the user.
     * @param schemaId id of schema given during the creation of the schema.
     * <br>
     * @return Completable future
     * @since 1.0
     */
    public static CompletableFuture<String> schemaGetAttributes( String sourceId, String schemaId) throws VcxException {
        ParamGuard.notNullOrWhiteSpace(sourceId, "sourceId");
        logger.debug("schemaGetAttributes() called with: sourceId = [" + sourceId + "], schemaHandle = [" + schemaId + "]");
        CompletableFuture<String> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);
        int result = LibVcx.api.vcx_schema_get_attributes(commandHandle, sourceId,schemaId, schemaGetAttributesCB);
        checkResult(result);
        return future;
    }

    private static Callback schemaGetSchemaID = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int commandHandle, int err, String schemaId) {
            logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], schemaId = [" + schemaId + "]");
            CompletableFuture<String> future = (CompletableFuture<String>) removeFuture(commandHandle);
            if (!checkCallback(future, err)) return;
            future.complete(schemaId);
        }
    };
    /**
     * Retrieves schema's id
     *
     * @param schemaHandle Schema handle that was provided during creation. Used to access proof object
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer to schemaCreate example for credential demo"
     * @see #schemaCreate
     */
    public static CompletableFuture<String> schemaGetSchemaId( int schemaHandle) throws VcxException {
        ParamGuard.notNull(schemaHandle, "SchemaHandle");
        logger.debug("schemaGetSchemaId() called with: schemaHandle = [" + schemaHandle + "]");
        CompletableFuture<String> future = new CompletableFuture<>();
        int commandHandle = addFuture(future);
        int result = LibVcx.api.vcx_schema_get_schema_id(commandHandle,schemaHandle, schemaGetSchemaID);
        checkResult(result);
        return future;
    }
    /**
     * Releases the schema object by de-allocating memory
     *
     * @param schemaHandle Schema handle that was provided during creation. Used to access schema object
     * @return Success
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @see "Refer to schemaCreate example for credential demo"
     * @see #schemaCreate
     */
    public static int schemaRelease(
            int schemaHandle
    ) throws VcxException {
        ParamGuard.notNull(schemaHandle, "schemaHandle");
        logger.debug("schemaRelease() called with: schemaHandle = [" + schemaHandle + "]");

        int result = LibVcx.api.vcx_schema_release(schemaHandle);
        checkResult(result);

        return result;
    }

    private static Callback schemaPrepareForEndorserCB = new Callback() {
        @SuppressWarnings({"unused", "unchecked"})
        public void callback(int command_handle, int err, int handle, String transaction) {
            logger.debug("callback() called with: command_handle = [" + command_handle + "], err = [" + err + "], handle = [" + handle + "], transaction = [" + transaction + "]");
            CompletableFuture<SchemaPrepareForEndorserResult> future = (CompletableFuture<SchemaPrepareForEndorserResult>) removeFuture(command_handle);
            if (!checkCallback(future, err)) return;
            SchemaPrepareForEndorserResult result = new SchemaPrepareForEndorserResult(handle, transaction);
            future.complete(result);
        }
    };
    /**
     * Create a new Schema object that will be published by Endorser later.<br>
     * Note that Schema can't be used for credential issuing until it will be published on the ledger.
     *
     * @param sourceId Enterprise's personal identification for the user.
     * @param schemaName version of schema.
     * @param version version of schema.
     * @param data list of attributes that will make up the schema (the number of attributes should be less or equal than 125).
     * @param endorser DID of the Endorser that will submit the transaction.
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @since 1.0
     */
    public static CompletableFuture<SchemaPrepareForEndorserResult> schemaPrepareForEndorser(String sourceId,
                                                                                             String schemaName,
                                                                                             String version,
                                                                                             String data,
                                                                                             String endorser) throws VcxException {
        ParamGuard.notNullOrWhiteSpace(sourceId, "sourceId");
        ParamGuard.notNull(schemaName, "schemaName");
        ParamGuard.notNull(version, "version");
        ParamGuard.notNull(data, "data");
        ParamGuard.notNull(endorser, "endorserendorser");
	    logger.debug("schemaCreate() called with: sourceId = [" + sourceId + "], schemaName = [" + schemaName + "], version = [" + version + "]" + " data = <" + data + ">" + " endorser = <" + endorser + ">");
        CompletableFuture<SchemaPrepareForEndorserResult> future = new CompletableFuture<SchemaPrepareForEndorserResult>();
        int commandHandle = addFuture(future);

        int result = LibVcx.api.vcx_schema_prepare_for_endorser(
                commandHandle,
		        sourceId,
		        schemaName,
		        version,
		        data,
		        endorser,
		        schemaPrepareForEndorserCB);
        checkResult(result);

        return future;
    }

	private static Callback vcxIntegerCB = new Callback() {
		@SuppressWarnings({"unused", "unchecked"})
		public void callback(int commandHandle, int err, int s) {
			logger.debug("callback() called with: commandHandle = [" + commandHandle + "], err = [" + err + "], s = [" + s + "]");
			CompletableFuture<Integer> future = (CompletableFuture<Integer>) removeFuture(commandHandle);
			if (!checkCallback(future, err)) return;
			Integer result = s;
			future.complete(result);
		}
	};
    /**
     * Checks if schema is published on the Ledger and updates the  state<br>
     *
     * @param schemaHandle Schema handle that was provided during creation. Used to access schema object
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @since 1.0
     */
	public static CompletableFuture<Integer> schemaUpdateState(int schemaHandle) throws VcxException {
		logger.debug("vcxSchemaUpdateState() called with: schemaHandle = [" + schemaHandle + "]");
		CompletableFuture<Integer> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_schema_update_state(
				commandHandle,
				schemaHandle,
				vcxIntegerCB
		);
		checkResult(result);
		return future;
	}
    /**
     * Get the current state of the schema object<br>
     *
     * @param schemaHandle Schema handle that was provided during creation. Used to access schema object
     * @return completable future
     * @throws VcxException Thrown if an error occurs when calling the underlying SDK.
     * @since 1.0
     */
	public static CompletableFuture<Integer> schemaGetState(int schemaHandle) throws VcxException {
		logger.debug("schemaGetState() called with: schemaHandle = [" + schemaHandle + "]");
		CompletableFuture<Integer> future = new CompletableFuture<>();
		int commandHandle = addFuture(future);

		int result = LibVcx.api.vcx_schema_get_state(
				commandHandle,
				schemaHandle,
				vcxIntegerCB
		);
		checkResult(result);
		return future;
	}
}
