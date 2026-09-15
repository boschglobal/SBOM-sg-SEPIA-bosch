package org.openchainproject.sepia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CDQSpdx23Manifest {

    @Valid
    @NotNull(message = "creationInfo is mandatory")
    private CreationInfo creationInfo;

    @Valid
    @NotEmpty(message = "packages is mandatory")
    private List<PackageInfo> packages;

    // =========================================================
    // Getter Setter
    // =========================================================

    public CreationInfo getCreationInfo() {
        return creationInfo;
    }

    public void setCreationInfo(CreationInfo creationInfo) {
        this.creationInfo = creationInfo;
    }

    public List<PackageInfo> getPackages() {
        return packages;
    }

    public void setPackages(List<PackageInfo> packages) {
        this.packages = packages;
    }

    // =========================================================
    // CreationInfo
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreationInfo {

        @NotEmpty(message = "creationInfo.creators is mandatory")
        private List<String> creators;

        // Getter Setter

        public List<String> getCreators() {
            return creators;
        }

        public void setCreators(List<String> creators) {
            this.creators = creators;
        }
    }

    // =========================================================
    // PackageInfo
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PackageInfo {

        @Valid
        @NotEmpty(message = "package.externalRefs is mandatory")
        private List<ExternalRef> externalRefs;

        @NotBlank(message = "package.name is mandatory")
        private String name;

        @NotBlank(message = "package.versionInfo is mandatory")
        private String versionInfo;

        @NotBlank(message = "package.primaryPackagePurpose is mandatory")
        private String primaryPackagePurpose;

        @NotBlank(message = "package.licenseConcluded is mandatory")
        private String licenseConcluded;

        // =====================================================
        // Getter Setter
        // =====================================================

        public List<ExternalRef> getExternalRefs() {
            return externalRefs;
        }

        public void setExternalRefs(List<ExternalRef> externalRefs) {
            this.externalRefs = externalRefs;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersionInfo() {
            return versionInfo;
        }

        public void setVersionInfo(String versionInfo) {
            this.versionInfo = versionInfo;
        }

        public String getPrimaryPackagePurpose() {
            return primaryPackagePurpose;
        }

        public void setPrimaryPackagePurpose(String primaryPackagePurpose) {
            this.primaryPackagePurpose = primaryPackagePurpose;
        }

        public String getLicenseConcluded() {
            return licenseConcluded;
        }

        public void setLicenseConcluded(String licenseConcluded) {
            this.licenseConcluded = licenseConcluded;
        }
    }

    // =========================================================
    // ExternalRef
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExternalRef {

        @NotBlank(message =
                "externalRef.referenceCategory is mandatory")
        private String referenceCategory;

        // Getter Setter

        public String getReferenceCategory() {
            return referenceCategory;
        }

        public void setReferenceCategory(String referenceCategory) {
            this.referenceCategory = referenceCategory;
        }
    }
}