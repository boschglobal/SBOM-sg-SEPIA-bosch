// SPDX-FileCopyrightText: Copyright (C) 2025 Contributors to SEPIA
//
// SPDX-License-Identifier: MIT

package org.openchainproject.sepia.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)

public class BomFilesInputModel {
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String inputType;
	
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String schemaType;
	
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String schemaVersion;
	
	@JsonIgnore
	private MultipartFile sbomFile;
	
	private MultipartFile schemaFile;
	
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private boolean schema;
	
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private int index;
	
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	//private String timestamp;
	private String sessionId;
	
	@Schema(hidden = true)
	private String sbomJsonString;
	
	@Schema(hidden = true)
	private String schemaJsonString;
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private boolean valid;
	private String filePath;
	
	@Schema(hidden = true)
	private boolean validatedAlready;
	
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	private String sbomFileName;
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String schemaFileName;
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private List<ErrorModel> errorDetails = new ArrayList<>();
	
	@Schema(hidden = true)
	private String dirName;
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private List<ChangeLog> changeLogsList;
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String fileHash;
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private List<ErrorModel> customErrorDetails;
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private Object sbomJson;
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private Object schemaJson;
	
	public void addErrorModel(List<ErrorModel> errorModel) {
        if (this.errorDetails == null) {
            this.errorDetails = new ArrayList<>();
        }
        this.errorDetails.addAll(errorModel);
    }
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String logOutputPath;
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String fossidServer;
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String apiKey;
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String scanCode;
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String ntid;
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String message;
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private Integer status;
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private List<org.openchainproject.sepia.loss.LossEvent> lossEvent = new ArrayList<>();
	
	@Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private List<org.openchainproject.sepia.loss.ConversionDelta> conversionDeltas = new ArrayList<>();
}