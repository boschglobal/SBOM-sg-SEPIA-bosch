/*
 Parts of this file are created by genAI by using GitHub Copilot. 
 This notice needs to remain attached to any reproduction of or excerpt from this file.
 */
// SPDX-FileCopyrightText: Copyright (C) 2025 Contributors to SEPIA
//
// SPDX-License-Identifier: MIT

package org.openchainproject.sepia.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.openchainproject.sepia.model.BomFilesInputModel;
import org.openchainproject.sepia.model.ChangeLog;
import org.openchainproject.sepia.service.ConversionService;
import org.openchainproject.sepia.service.SbomUtilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;


import org.openchainproject.sepia.util.Constants;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.ConstraintViolation;
import java.util.Set;

import org.springframework.http.MediaType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 
 * 
 *
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SbomUtilityController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SbomUtilityController.class);
    

	
	@Autowired
	private SbomUtilityService sbomUtilityService;
	
	@Autowired
	private ConversionService service;

    
	
	@Operation(summary = "Health check for SBOM Validator API", description = "Returns the health status of the SBOM Validator API along with the user ID from the request headers.")
	@GetMapping("/health")
    public ResponseEntity<Map<String, String>> getHealth(@RequestHeader HttpHeaders headers) {
        // Perform any necessary health checks here
        // For simplicity, we'll just return a 200 OK status
		Map<String, String> response = new HashMap<>();
		
		List<String> ivUserList = headers.get("iv-user");
		String userNtId = null;
		if (ivUserList != null && !ivUserList.isEmpty()) {
			userNtId = ivUserList.get(0);
		}
		
        response.put("status", "Server is running");
        response.put("userId", userNtId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

	@Hidden
	@PostMapping(path = "/uploadInputFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BomFilesInputModel uploadInputFile(@RequestParam("file") Optional<MultipartFile[]> inputFile,
			@RequestParam("postData") String postData) {
		BomFilesInputModel sbomInputModel = null;
		try {
			ObjectMapper mapper = new ObjectMapper();

			sbomInputModel = mapper.readValue(postData, BomFilesInputModel.class);

			// Validate that the JSON content actually matches the declared schemaType
			String contentMismatch = validateSbomContentMatchesSchemaType(mapper, inputFile,
					sbomInputModel.getSchemaType());
			if (contentMismatch != null) {
				sbomInputModel.setMessage(contentMismatch);
				sbomInputModel.setStatus(HttpStatus.BAD_REQUEST.value());
				sbomInputModel.setValid(false);
				return sbomInputModel;
			}

			sbomInputModel = sbomUtilityService.uploadInputFile(inputFile, sbomInputModel, false);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while the given information inside uploadInputFile()", e);
			sbomInputModel.setMessage(e.getMessage());
			sbomInputModel.setStatus(HttpStatus.BAD_REQUEST.value());
		}
		return sbomInputModel;
	}

	@Hidden
	@PostMapping(path = "/deleteSbomEntry")
	public BomFilesInputModel deleteSbomEntry(@RequestParam("postData") String postData) {

		BomFilesInputModel sbomInputModel = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			sbomInputModel = mapper.readValue(postData, BomFilesInputModel.class);

			sbomInputModel = sbomUtilityService.deleteSbomEntry(sbomInputModel);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while the given information inside deleteSbomEntry()", e);
		}
		return sbomInputModel;
	}

	@Hidden
	@PostMapping(path = "/clearSession")
	public void clearSession(@RequestParam("postData") String postData) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			
			BomFilesInputModel sbomInputModel = mapper.readValue(postData, BomFilesInputModel.class);
			sbomUtilityService.clearSession(sbomInputModel);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while the given information inside clearSession()", e);
		}
	}

	@Hidden
	@PostMapping(path = "/validateSboms", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BomFilesInputModel validateSboms(@RequestParam("postData") String postData) {
		BomFilesInputModel sbomInputModel = null;
		try {
			ObjectMapper mapper = new ObjectMapper();

			sbomInputModel = mapper.readValue(postData, BomFilesInputModel.class);
			sbomInputModel = sbomUtilityService.validateSboms(sbomInputModel, false, false);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while the given information inside validateSboms()", e);
	        if (sbomInputModel == null) {
	        	sbomInputModel = new BomFilesInputModel();
	            //throw new NullPointerException("sbomInputModel is null due to an error during deserialization or validation.");
	        }
			sbomInputModel.setMessage("An error occurred during validation. Please check your input file.");
			sbomInputModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return sbomInputModel;
	}

	@Hidden
	@PostMapping(path = "/fetchErrorDetails")
	public BomFilesInputModel fetchErrorDetails(@RequestParam("postData") String postData) {
		BomFilesInputModel sbomInputModel = new BomFilesInputModel();
		try {

			ObjectMapper mapper = new ObjectMapper();

			sbomInputModel = mapper.readValue(postData, BomFilesInputModel.class);
			sbomInputModel = sbomUtilityService.fetchErrorDetails(sbomInputModel);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while the given information inside fetchErrorDetails()", e);
		}

		return sbomInputModel;
	}

	@Hidden
	@PostMapping("/fetchJsonContent")
	public BomFilesInputModel fetchJsonContent(@RequestParam("postData") String postData) {
		BomFilesInputModel sbomInputModel = new BomFilesInputModel();
		try {

			ObjectMapper mapper = new ObjectMapper();

			sbomInputModel = mapper.readValue(postData, BomFilesInputModel.class);
			sbomInputModel = sbomUtilityService.fetchJsonContent(sbomInputModel);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while the given information inside fetchJsonContent()", e);
		}

		return sbomInputModel;
	}

	@Operation(summary = "Merge 2 or more valid CycloneDx V1.4 SBOMs with this API", description = "This endpoint allows you to merge multiple valid CycloneDx V1.4 SBOMs into a single SBOM. Provide the SBOMs and metadata in the request body.",hidden = true)
	@PostMapping("/mergeCyclonedx")
	public BomFilesInputModel mergeCyclonedxSboms(@RequestParam("postData") String postData,
			@RequestParam("bomMetadata") String bomMetadata, @RequestParam(value = "isFromApp", required = false) boolean isFromApp) {
		List<BomFilesInputModel> bomInputList = new ArrayList<>();
		BomFilesInputModel mergedBomInput = new BomFilesInputModel();

		try {
			ObjectMapper mapper = new ObjectMapper();

			bomInputList = mapper.readValue(postData, new TypeReference<List<BomFilesInputModel>>() {
			});
			if(bomInputList.get(0).getSchemaType().equalsIgnoreCase(Constants.CYCLONEDX_LC)) {
				mergedBomInput = sbomUtilityService.mergeSboms(bomInputList, bomMetadata, Constants.CYCLONEDX_LC, isFromApp);
			}else {
				mergedBomInput = sbomUtilityService.mergeSboms(bomInputList, bomMetadata, Constants.CDQ_CYDX1_6_LC, isFromApp);
			}
			
			if(!isFromApp) {
				mergedBomInput.setSbomJsonString(null);
			}
		} catch (Exception e) {
			LOGGER.error("Exception occurred while merging the selected SBOMs inside mergeCyclonedxSboms()", e);
		}
		return mergedBomInput;
	}

	@Operation(summary = "Merge 2 or more valid SPDX V2.3 SBOMs with this API", description = "This endpoint allows you to merge multiple valid SPDX V2.3 SBOMs into a single SBOM. Provide the SBOMs and metadata in the request body.",hidden = true)
	@PostMapping("/mergeSpdx")
	public BomFilesInputModel mergeSpdxSboms(@RequestParam("postData") String postData,
			@RequestParam("bomMetadata") String bomMetadata, @RequestParam(value = "isFromApp", required = false) boolean isFromApp) {
		List<BomFilesInputModel> bomInputList = new ArrayList<>();
		BomFilesInputModel mergedBomInput = new BomFilesInputModel();

		try {
			ObjectMapper mapper = new ObjectMapper();

			bomInputList = mapper.readValue(postData, new TypeReference<List<BomFilesInputModel>>() {
			});

			mergedBomInput = sbomUtilityService.mergeSboms(bomInputList, bomMetadata, Constants.SPDX, isFromApp);
			if(!isFromApp) {
				mergedBomInput.setSbomJsonString(null);
			}
		} catch (Exception e) {
			LOGGER.error("Exception occurred while merging the selected SBOMs inside mergeSpdxSboms() ", e);
		}
		return mergedBomInput;
	}
	
	@Operation(summary = "Convert SBOMs between CycloneDx V1.6 and SPDX V2.3", description = "This endpoint allows you to convert a valid CycloneDx V1.6 SBOM to SPDX V2.3 or vice versa. Provide the SBOM and its schema type in the request body.",hidden = true)
	@PostMapping("/convertSbom")
	public BomFilesInputModel convertSbom(@RequestParam("postData") String postData,@RequestParam(value = "isFromApp", required = false) boolean isFromApp) {
		BomFilesInputModel sbomInputModel = new BomFilesInputModel();
		BomFilesInputModel sbomConvertedModel = new BomFilesInputModel();
		try {
			ObjectMapper mapper = new ObjectMapper();
			
			sbomInputModel = mapper.readValue(postData, BomFilesInputModel.class);
			ObjectNode source = (ObjectNode) mapper.readTree(sbomInputModel.getSbomJsonString());
			if(sbomInputModel.getSchemaType().equalsIgnoreCase(Constants.CDQ_CYDX1_6_LC)) {
				sbomConvertedModel = service.convert(source,Constants.CYCLONEDX_LC,Constants.VER1_6,Constants.SPDX_LC,Constants.VER2_3);
				sbomConvertedModel.setSchemaType(Constants.CDQ_SPDX2_3_LC);
				sbomConvertedModel.setSchemaVersion("2.3");
				sbomConvertedModel = sbomUtilityService.validateSboms(sbomConvertedModel, true, false);
				
			}else {
				sbomConvertedModel = service.convert(source,Constants.SPDX_LC,Constants.VER2_3,Constants.CYCLONEDX_LC,Constants.VER1_6);
				sbomConvertedModel.setSchemaType(Constants.CDQ_CYDX1_6_LC);
				sbomConvertedModel.setSchemaVersion("2.3");
				sbomConvertedModel = sbomUtilityService.validateSboms(sbomConvertedModel, true, false);
				
			}
			
		} catch (Exception e) {
			LOGGER.error("Exception occurred while the given information inside convertSbom()", e);
		}

		return sbomConvertedModel;
	}
	

	@Hidden
	@PostMapping(path = "/replaceFile")
	public BomFilesInputModel replaceFile(@RequestParam("postData") String postData) {
		BomFilesInputModel sbomInputModel = new BomFilesInputModel();
		try {

			ObjectMapper mapper = new ObjectMapper();

			sbomInputModel = mapper.readValue(postData, BomFilesInputModel.class);
			sbomInputModel = sbomUtilityService.replaceFile(sbomInputModel);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while the replacing file with updated content", e);
		}

		return sbomInputModel;
	}

	@Hidden
	@PostMapping(path = "/getJsonDifferences")
	public List<ChangeLog> getJsonDifferences(@RequestParam("currentValue") String currentValue,
			@RequestParam("postData") String postData,@RequestParam("isMerge") Boolean isMerge) {

		List<ChangeLog> changeLogsList = new ArrayList<>();
		BomFilesInputModel sbomInputModel = new BomFilesInputModel();
		try {
			ObjectMapper mapper = new ObjectMapper();
			sbomInputModel = mapper.readValue(postData, BomFilesInputModel.class);
			changeLogsList = sbomUtilityService.getJsonDifferences(currentValue,sbomInputModel,isMerge);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while the replacing file with updated content inside getJsonDifferences()", e);
		}

		return changeLogsList;
	}

	@Hidden
	@PostMapping(path = "/prepareForDownload")
	public BomFilesInputModel prepareForDownload(@RequestParam("currentValue") String currentValue,
			@RequestParam("postData") String postData) {
		ObjectMapper mapper = new ObjectMapper();

		BomFilesInputModel bomFilesInputModel = null;
		try {
			
			bomFilesInputModel = mapper.readValue(postData, BomFilesInputModel.class);
			bomFilesInputModel = sbomUtilityService.prepareForDownload(currentValue, bomFilesInputModel);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while the preparing content for download", e);
		}
		
		return bomFilesInputModel;
	}
	
	@Operation(summary = "Upload an input SBOM of type CycloneDx V1.4,CycloneDx V1.6 or SPDX V2.3 and get it validated", description = "This endpoint allows you to upload a single SBOM file (CycloneDx V1.4, CycloneDx V1.6, or SPDX V2.3) along with its metadata in the request body. The API will validate the uploaded SBOM against its declared schema type and return the validation results.")
	@PostMapping(path = "/uploadAndValidate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ObjectNode uploadAndValidate(
			@Parameter(description = "Select the JSON file to upload")
            @RequestPart("file") MultipartFile inputFile,
            @Parameter(description = "Enter the postData in the following JSON string format: {\"schemaType\":\"cyclonedx\",\"sessionId\":\"your-session-id\"}.\n\n"
            		+ "Allowed values for schemaType(*): cyclonedx, spdx, cdqspdx2.3, cdqcydx.\n\n"
            				+ "The sessionId is optional.")
            @RequestPart("postData") String postData
            ) {
		BomFilesInputModel sbomInputModel = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			sbomInputModel = mapper.readValue(postData, BomFilesInputModel.class);

			// --- Input validation -------------------------------------------------
			// 1) Validate schemaType
			String schemaType = sbomInputModel.getSchemaType();
			List<String> allowedSchemaTypes = java.util.Arrays.asList(
					Constants.CYCLONEDX_LC,        // "cyclonedx"
					Constants.SPDX_LC,             // "spdx"
					Constants.CDQ_SPDX2_3_LC,      // "cdqspdx2.3"
					Constants.CDQ_CYDX1_6_LC       // "cdqcydx"
			);
			boolean schemaTypeValid = schemaType != null
					&& allowedSchemaTypes.stream().anyMatch(s -> s.equalsIgnoreCase(schemaType));
			if (!schemaTypeValid) {
				return buildUploadErrorResponse(mapper, sbomInputModel,
						"Invalid schemaType '" + schemaType + "'. Allowed values: " + allowedSchemaTypes);
			}

			// 2) Validate uploaded file is a .json file
			String uploadedFileName = inputFile != null ? inputFile.getOriginalFilename() : null;
			if (uploadedFileName == null || uploadedFileName.trim().isEmpty()) {
				uploadedFileName = sbomInputModel.getSbomFileName();
			}
			if (uploadedFileName == null || !uploadedFileName.toLowerCase().endsWith(".json")) {
				return buildUploadErrorResponse(mapper, sbomInputModel,
						"Invalid file '" + uploadedFileName + "'. Only .json files are accepted for upload.");
			}

			// 3) Validate that the JSON content actually matches the declared schemaType
			Optional<MultipartFile[]> multipartInputFile = Optional.of(new MultipartFile[] { inputFile });
			String contentMismatch = validateSbomContentMatchesSchemaType(mapper, multipartInputFile, schemaType);
			if (contentMismatch != null) {
				return buildUploadErrorResponse(mapper, sbomInputModel, contentMismatch);
			}
			// ----------------------------------------------------------------------

			sbomInputModel = sbomUtilityService.uploadInputFile(multipartInputFile, sbomInputModel, true);
		} catch (Exception e) {
			LOGGER.error("Exception occurred while the given information inside uploadAndValidate()", e);
			return buildUploadErrorResponse(mapper, sbomInputModel,
					"Error processing uploaded file: " + e.getMessage());
		}

		// Hide internal fields from the /uploadAndValidate response
		ObjectNode responseNode = mapper.valueToTree(sbomInputModel);
		responseNode.remove("schema");
		responseNode.remove("filePath");
		responseNode.remove("validatedAlready");
		responseNode.remove("status");
		return responseNode;
	}

	private ObjectNode buildUploadErrorResponse(ObjectMapper mapper, BomFilesInputModel sbomInputModel, String message) {
		ObjectNode errorNode = mapper.createObjectNode();
		if (sbomInputModel != null) {
			if (sbomInputModel.getSbomFileName() != null) {
				errorNode.put("sbomFileName", sbomInputModel.getSbomFileName());
			}
			if (sbomInputModel.getSchemaType() != null) {
				errorNode.put("schemaType", sbomInputModel.getSchemaType());
			}
			if (sbomInputModel.getSchemaVersion() != null) {
				errorNode.put("schemaVersion", sbomInputModel.getSchemaVersion());
			}
			if (sbomInputModel.getSessionId() != null) {
				errorNode.put("sessionId", sbomInputModel.getSessionId());
			}
			errorNode.put("index", sbomInputModel.getIndex());
		}
		errorNode.put("message", message);
		return errorNode;
	}

	/**
	 * Verifies that the uploaded JSON content matches the declared schemaType.
	 * Returns a human-readable error message if there is a mismatch, otherwise {@code null}.
	 *
	 * Detection rules:
	 *   - CycloneDX is identified by the top-level "bomFormat":"CycloneDX" and "specVersion" fields.
	 *   - SPDX is identified by the top-level "spdxVersion" field (e.g. "SPDX-2.2", "SPDX-2.3").
	 */
	private String validateSbomContentMatchesSchemaType(ObjectMapper mapper,
			Optional<MultipartFile[]> inputFile, String schemaType) {
		if (!inputFile.isPresent() || inputFile.get().length == 0 || inputFile.get()[0] == null) {
			return "Uploaded file is missing - cannot verify content against schemaType '" + schemaType + "'.";
		}
		MultipartFile file = inputFile.get()[0];
		JsonNode root;
		try {
			root = mapper.readTree(file.getInputStream());
		} catch (Exception e) {
			return "Uploaded file is not a valid JSON document: " + e.getMessage();
		}
		if (root == null || !root.isObject()) {
			return "Uploaded file is not a valid JSON object.";
		}

		String type = schemaType == null ? "" : schemaType.toLowerCase();
		if (Constants.CYCLONEDX_LC.equalsIgnoreCase(type)) {
			return checkCycloneDx(root, schemaType, "1.4");
		}
		if (Constants.CDQ_CYDX1_6_LC.equalsIgnoreCase(type)) {
			return checkCycloneDx(root, schemaType, "1.6");
		}
		if (Constants.SPDX_LC.equalsIgnoreCase(type) || Constants.SPDX2_2_LC.equalsIgnoreCase(type)) {
			String expected = Constants.SPDX2_2_LC.equalsIgnoreCase(type) ? "SPDX-2.2" : null;
			return checkSpdx(root, schemaType, expected);
		}
		if (Constants.CDQ_SPDX2_3_LC.equalsIgnoreCase(type)) {
			return checkSpdx(root, schemaType, "SPDX-2.3");
		}
		if (Constants.AUTOMOTIVE_CYDX1_6_LC.equalsIgnoreCase(type)) {
			return checkCycloneDx(root, schemaType, "1.6");
		}
		if (Constants.AUTOMOTIVE_SPDX2_3_LC.equalsIgnoreCase(type)) {
			return checkSpdx(root, schemaType, "SPDX-2.3");
		}
		
		return null;
	}

	private String checkCycloneDx(JsonNode root, String schemaType, String expectedSpecVersion) {
		String bomFormat = root.path("bomFormat").asText("");
		String specVersion = root.path("specVersion").asText("");
		if (!"CycloneDX".equalsIgnoreCase(bomFormat)) {
			return "Uploaded file is not a valid CycloneDX " + expectedSpecVersion
					+ " JSON (missing or wrong 'bomFormat'). schemaType '" + schemaType
					+ "' expects a CycloneDX " + expectedSpecVersion + " SBOM.";
		}
		if (!expectedSpecVersion.equals(specVersion)) {
			return "Uploaded file is CycloneDX specVersion '" + specVersion + "', but schemaType '"
					+ schemaType + "' expects CycloneDX " + expectedSpecVersion + ".";
		}
		return null;
	}

	private String checkSpdx(JsonNode root, String schemaType, String expectedSpdxVersion) {
		String spdxVersion = root.path("spdxVersion").asText("");
		if (spdxVersion.isEmpty()) {
			return "Uploaded file is not a valid SPDX JSON (missing 'spdxVersion'). schemaType '"
					+ schemaType + "' expects an SPDX SBOM.";
		}
		if (expectedSpdxVersion != null && !expectedSpdxVersion.equalsIgnoreCase(spdxVersion)) {
			return "Uploaded file has spdxVersion '" + spdxVersion + "', but schemaType '"
					+ schemaType + "' expects " + expectedSpdxVersion + ".";
		}
		return null;
	}
	
	@Hidden
	@GetMapping(value = "/downloadUserManual", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public void downloaderUnattended(HttpServletRequest request, HttpServletResponse response) {
 
		try {
			response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=User_Manual_Beta.pdf");
			IOUtils.copy(this.getClass().getClassLoader().getResourceAsStream("User_Manual_Beta.pdf"),
					response.getOutputStream());
			response.getOutputStream().flush();
		} catch (IOException e) {
			LOGGER.error("Error occured during file download - downloaderUnattended() :", e);
		}
 
	}
		
	@Operation(summary = "Upload a BOM file and Schema file and and get it validated", description = "This endpoint allows you to upload a single BOM file along with its corresponding schema file. The API will validate the uploaded BOM against the provided schema and return the validation results.")
	@PostMapping(path = "/customValidate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ObjectNode customValidate(
			    @Parameter(description = "Select the JSON file to upload")
				@RequestPart("file") MultipartFile inputFile,
				@Parameter(description = "Select the Schema JSON file to upload")
				@RequestParam("schemaFile") MultipartFile schemaFile,
				@Parameter(description = "Enter the postData in the following JSON string format: {\"schemaType\":\"custom\"}.",
						schema = @Schema(type = "string", defaultValue = "{\"schemaType\":\"custom\"}", example = "{\"schemaType\":\"custom\"}"))
				@RequestPart("postData") String postData) {
			BomFilesInputModel sbomInputModel = null;
			ObjectMapper mapper = new ObjectMapper();
			try {
				sbomInputModel = mapper.readValue(postData, BomFilesInputModel.class);
				// --- Input validation -------------------------------------------------
				// 1) Validate schemaType
				String schemaType = sbomInputModel.getSchemaType();
				List<String> allowedSchemaTypes = java.util.Arrays.asList(Constants.CUSTOM);
				boolean schemaTypeValid = schemaType != null
						&& allowedSchemaTypes.stream().anyMatch(s -> s.equalsIgnoreCase(schemaType));
				if (!schemaTypeValid) {
					return buildUploadErrorResponse(mapper, sbomInputModel,
							"Invalid schemaType '" + schemaType + "'. Allowed values: " + allowedSchemaTypes);
				}
	 
				// 2) Validate uploaded file is a .json file
				String uploadedFileName = inputFile != null ? inputFile.getOriginalFilename() : null;
				if (uploadedFileName == null || uploadedFileName.trim().isEmpty()) {
					uploadedFileName = sbomInputModel.getSbomFileName();
				}
				if (uploadedFileName == null || !uploadedFileName.toLowerCase().endsWith(".json")) {
					return buildUploadErrorResponse(mapper, sbomInputModel,
							"Invalid file '" + uploadedFileName + "'. Only .json files are accepted for upload.");
				}else {
					Optional<MultipartFile[]> multipartInputFile = Optional.of(new MultipartFile[] { inputFile });
					sbomInputModel = sbomUtilityService.uploadInputFile(multipartInputFile, sbomInputModel, true);
				}
	 
				// 3) Read the custom schema file and set it in the model

				String uploadedschemaFileName = schemaFile != null ? schemaFile.getOriginalFilename() : null;
				if(uploadedschemaFileName!= null && !uploadedschemaFileName.trim().isEmpty() && uploadedschemaFileName.toLowerCase().endsWith(".json")) {
					String schemaContent = new String(schemaFile.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
					sbomInputModel.setSchemaJsonString(schemaContent);
					sbomInputModel.setSchema(true);
					Optional<MultipartFile[]> multipartSchemaInputFile = Optional.of(new MultipartFile[] { schemaFile });
					sbomInputModel = sbomUtilityService.uploadInputFile(multipartSchemaInputFile, sbomInputModel, true);
				} else {
					return buildUploadErrorResponse(mapper, sbomInputModel,
							"Custom schema file (Schemafile) is missing or empty.");
				}
			} catch (Exception e) {
				LOGGER.error("Exception occurred while the given information inside customValidate()", e);
				return buildUploadErrorResponse(mapper, sbomInputModel,
						"Error processing uploaded file: " + e.getMessage());
			}
			// Hide internal fields from the /customValidate response
			ObjectNode responseNode = mapper.valueToTree(sbomInputModel);
			responseNode.remove("schema");
			responseNode.remove("filePath");
			responseNode.remove("validatedAlready");
			responseNode.remove("status");
			responseNode.remove("schemaJsonString");
			return responseNode;
		}
	
	@Operation(summary = "Upload SEPIA SPDX V2.3,SEPIA CycloneDX V1.6 BOM files for Convert operation through API", description = "This endpoint allows you to upload a single SBOM file (CycloneDx V1.6 or SPDX V2.3) along with its metadata in the request body. The API will validate the uploaded SBOM against its declared schema type and return the validation results.")
	@PostMapping(path = "/validateAndConvert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ObjectNode validateAndConvert(
			   @Parameter(description = "Select the JSON file to upload")
			   @RequestPart("file") MultipartFile inputFile,
			   @Parameter(description = "Enter the postData in the following JSON string format: {\"schemaType\":\"cdqcydx\"}.\n\n"
	            		+ "Allowed values for schemaType(*):cdqspdx2.3, cdqcydx.")
			   @RequestPart("postData") String postData) {
		       
		       BomFilesInputModel sbomInputModel = null;
		       BomFilesInputModel sbomConvertedModel = new BomFilesInputModel();
		       ObjectMapper mapper = new ObjectMapper();
		       try {
		    	   sbomInputModel = mapper.readValue(postData, BomFilesInputModel.class);
		    	   
				   // --- Input validation -------------------------------------------------
				   // 1) Validate schemaType
				   String schemaType = sbomInputModel.getSchemaType();
				   List<String> allowedSchemaTypes = java.util.Arrays.asList(
						   Constants.CDQ_SPDX2_3_LC,      // "cdqspdx2.3"
						   Constants.CDQ_CYDX1_6_LC       // "cdqcydx"
				   );
				   boolean schemaTypeValid = schemaType != null
						   && allowedSchemaTypes.stream().anyMatch(s -> s.equalsIgnoreCase(schemaType));
				   if (!schemaTypeValid) {
					   return buildUploadErrorResponse(mapper, sbomInputModel,
							   "Invalid schemaType '" + schemaType + "'. Allowed values: " + allowedSchemaTypes);
				   }
				   
				   // 2) Validate all uploaded files are .json files
				   String uploadedFileName = inputFile != null ? inputFile.getOriginalFilename() : null;
					if (uploadedFileName == null || uploadedFileName.trim().isEmpty()) {
						uploadedFileName = sbomInputModel.getSbomFileName();
					}
					if (uploadedFileName == null || !uploadedFileName.toLowerCase().endsWith(".json")) {
						return buildUploadErrorResponse(mapper, sbomInputModel,
								"Invalid file '" + uploadedFileName + "'. Only .json files are accepted for upload.");
					}
				   // 3) Validate that the JSON content actually matches the declared schemaType
				   String contentMismatch = null;
				   try {
					   // Use a single-file Optional for validation helper
					   MultipartFile[] singleFileArr = new MultipartFile[] { inputFile };
					   contentMismatch = validateSbomContentMatchesSchemaType(mapper, Optional.of(singleFileArr), schemaType);
				   } catch (Exception ex) {
					   contentMismatch = "Error reading file '" + uploadedFileName + ex.getMessage();
				   }
				   if (contentMismatch != null) {
					   return buildUploadErrorResponse(mapper, sbomInputModel,
							   "File '" + uploadedFileName  + " failed in schemaType validation: " + contentMismatch);
				   }
				   
				   sbomConvertedModel = sbomUtilityService.validateAndConvertFromAPI(inputFile, sbomInputModel);
		    	   
		       } catch (Exception e) {
				   LOGGER.error("Exception occurred while the given information inside validateAndConvert()", e);
				   return buildUploadErrorResponse(mapper, sbomInputModel,
						   "Error processing uploaded file: " + e.getMessage());
			   }
		        // Hide internal fields from the /customValidate response
		        boolean hasValid = false;
				ObjectNode responseNode = mapper.valueToTree(sbomConvertedModel);
				responseNode.remove("schema");
				responseNode.remove("filePath");
				responseNode.remove("validatedAlready");
				responseNode.remove("status");
				responseNode.remove("schemaJsonString");
				responseNode.remove("sbomJsonString");
				// Check if any model is invalid
				if (sbomConvertedModel != null && sbomConvertedModel.getSbomFileName() != null) {
					hasValid = true;
				}
				if (hasValid) {
					responseNode.put("message", "Convert operation is successful and converted SBOM available in sbomJson property of the response. Refer lossEvent,conversionDeltas,errorDetails properties for more details");
				} else {
					responseNode.put("message", "Convert operation is failed due to invalid file");
				}
				return responseNode;
		
	}
		
	@Operation(summary = "Upload multiple BOM files for merge operation", description = "This endpoint allows you to upload multiple SBOM files (CycloneDx V1.4, CycloneDx V1.6, or SPDX V2.3) along with their metadata in the request body. The API will validate the uploaded SBOMs against their declared schema types and return the validation results.")
	@PostMapping(path = "/validateAndMerge", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ObjectNode validateAndMerge(
			       @Parameter(description = "Select the JSON files to upload")
				   @RequestPart("file") Optional<MultipartFile[]> inputFile,
				   @Parameter(description = "Select the SBOM Manifest (Information of merged SBOM metadata) JSON file to upload")
				   @RequestPart("manifestFile") MultipartFile manifestFile,
				   @Parameter(description = "Enter the postData in the following JSON string format: {\"schemaType\":\"cyclonedx\"}.\n\n"
		            		+ "Allowed values for schemaType(*): cyclonedx, spdx, cdqspdx2.3, cdqcydx.")
				   @RequestPart("postData") String postData) {

			   BomFilesInputModel sbomInputModel = null;
			   List<BomFilesInputModel> bomMergeResultList = new ArrayList<>();
			   ObjectMapper mapper = new ObjectMapper();
			   String manifestContent = null;
			   try {
				   sbomInputModel = mapper.readValue(postData, BomFilesInputModel.class);

				   // --- Input validation -------------------------------------------------
				   // 1) Validate schemaType
				   String schemaType = sbomInputModel.getSchemaType();
				   List<String> allowedSchemaTypes = java.util.Arrays.asList(
						   Constants.CYCLONEDX_LC,        // "cyclonedx"
						   Constants.SPDX_LC,             // "spdx"
						   Constants.CDQ_SPDX2_3_LC,      // "cdqspdx2.3"
						   Constants.CDQ_CYDX1_6_LC       // "cdqcydx"
				   );
				   boolean schemaTypeValid = schemaType != null
						   && allowedSchemaTypes.stream().anyMatch(s -> s.equalsIgnoreCase(schemaType));
				   if (!schemaTypeValid) {
					   return buildUploadErrorResponse(mapper, sbomInputModel,
							   "Invalid schemaType '" + schemaType + "'. Allowed values: " + allowedSchemaTypes);
				   }

				   // 2) Validate all uploaded files are .json files
				   if (!inputFile.isPresent() || inputFile.get().length == 0) {
					   return buildUploadErrorResponse(mapper, sbomInputModel,
							   "No files uploaded. At least two .json files are required for merge operation.");
				   }
				   if (inputFile.get().length < 2) {
					   return buildUploadErrorResponse(mapper, sbomInputModel,
							   "At least two .json files must be uploaded for merge operation.");
				   }
				   MultipartFile[] files = inputFile.get();
				   for (int i = 0; i < files.length; i++) {
					   MultipartFile file = files[i];
					   String uploadedFileName = file != null ? file.getOriginalFilename() : null;
					   if (uploadedFileName == null || uploadedFileName.trim().isEmpty()) {
						   uploadedFileName = sbomInputModel.getSbomFileName();
					   }
					   if (uploadedFileName == null || !uploadedFileName.toLowerCase().endsWith(".json")) {
						   return buildUploadErrorResponse(mapper, sbomInputModel,
								   "Invalid file '" + uploadedFileName + "' at index " + i + ". Only .json files are accepted for upload.");
					   }
					   // 3) Validate that the JSON content actually matches the declared schemaType
					   String contentMismatch = null;
					   try {
						   // Use a single-file Optional for validation helper
						   MultipartFile[] singleFileArr = new MultipartFile[] { file };
						   contentMismatch = validateSbomContentMatchesSchemaType(mapper, Optional.of(singleFileArr), schemaType);
					   } catch (Exception ex) {
						   contentMismatch = "Error reading file '" + uploadedFileName + "' at index " + i + ": " + ex.getMessage();
					   }
					   if (contentMismatch != null) {
						   return buildUploadErrorResponse(mapper, sbomInputModel,
								   "File '" + uploadedFileName + "' at index " + i + " failed schemaType validation: " + contentMismatch);
					   }
				   }
				   
				   // Validate manifestFile if present
				   if(manifestFile != null && !manifestFile.isEmpty()) {
					   String manifestFileName = manifestFile.getOriginalFilename();
					   if (manifestFileName == null || !manifestFileName.toLowerCase().endsWith(".json")) {
						   return buildUploadErrorResponse(mapper, sbomInputModel,
								   "Invalid manifestFile '" + manifestFileName + "'. Only .json files are accepted for upload.");
					   }
					   try {
						   manifestContent = new String(manifestFile.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
					   } catch (Exception e) {
						   return buildUploadErrorResponse(mapper, sbomInputModel,
								   "Manifest file is not a valid JSON document: " + e.getMessage());
					   }
					   // Additional manifest validation for schemaType
					   String manifestValidationError = validateManifestFileForSchemaType(manifestContent, schemaType,manifestFile);
					   if (manifestValidationError != null) {
						   return buildUploadErrorResponse(mapper, sbomInputModel, manifestValidationError);
					   }
				   }
				   
				bomMergeResultList = sbomUtilityService.validateAndMergeFromAPI(inputFile, manifestContent, sbomInputModel);
				   
			   } catch (Exception e) {
				   LOGGER.error("Exception occurred while the given information inside validateAndMerge()", e);
				   return buildUploadErrorResponse(mapper, sbomInputModel,
						   "Error processing uploaded file: " + e.getMessage());
			   }
			   
			   // Remove 'properties' from each result
			   ArrayNode arrayNode = mapper.createArrayNode();
			   boolean hasInvalid = false;
			   for (BomFilesInputModel model : bomMergeResultList) {
				   ObjectNode node = mapper.valueToTree(model);
				   node.remove("sbomFile");
				   node.remove("schemaJsonString");
				   node.remove("sbomJsonString");
				   node.remove("validatedAlready");
				   node.remove("schema");
				   node.remove("filePath");
				   arrayNode.add(node);
				   // Check if any model is invalid
				   if (model != null && !Boolean.TRUE.equals(model.isValid())) {
					   hasInvalid = true;
				   }
			   }
			   ObjectNode responseNode = mapper.createObjectNode();
			   if (hasInvalid) {
				   responseNode.put("message", "Merge operation is failed due to invalid files");
			   } else {
				   responseNode.put("message", "Merge operation is successful and merged SBOM available in sbomJson property of the response");
			   }
			   
			   responseNode.set("response", arrayNode);
			   // Return pretty-printed JSON
			   try {
				   String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseNode);
				   ObjectMapper prettyMapper = new ObjectMapper();
				   return (ObjectNode) prettyMapper.readTree(prettyJson);
			   } catch (Exception e) {
				   LOGGER.error("Error pretty-printing responseNode in validateAndMerge", e);
				   return responseNode;
			   }
		   }
	
	/**
	 * Validates that the uploaded manifest file matches the expected schemaType.
	 * Returns null if valid, or an error message if invalid.
	 *
	 * @param manifestContent The manifest file content as a String
	 * @param schemaType The schemaType (e.g., "cdqcydx", "cyclonedx", "cdqspdx2.3")
	 * @return null if valid, or error message if invalid
	 */
	private String validateManifestFileForSchemaType(String manifestContent, String schemaType,MultipartFile manifestFile) {
	    if (manifestContent == null || manifestContent.trim().isEmpty()) {
	        return "Manifest file is empty.";
	    }
	    ObjectMapper mapper = new ObjectMapper();
	    try {
	    	Set<String> errors =
	    			sbomUtilityService.manifestFileValidate(schemaType, manifestFile);
	        if (!errors.isEmpty()) {
	        	return "Manifest file is invalid : " + errors;
	        }
	    } catch (Exception e) {
	        return "Manifest file is not a valid JSON document: " + e.getMessage();
	    }
	    return null;
	 }
	}
