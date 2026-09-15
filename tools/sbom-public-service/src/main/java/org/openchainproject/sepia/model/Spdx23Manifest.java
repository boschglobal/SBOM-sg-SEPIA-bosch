package org.openchainproject.sepia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Spdx23Manifest {

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

        @NotBlank(message =
                "package.primaryPackagePurpose is mandatory")
        private String primaryPackagePurpose;

        @NotBlank(message =
                "package.licenseDeclared is mandatory")
        private String licenseDeclared;

        @NotBlank(message =
                "package.copyrightText is mandatory")
        private String copyrightText;

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

        public void setPrimaryPackagePurpose(
                String primaryPackagePurpose) {

            this.primaryPackagePurpose =
                    primaryPackagePurpose;
        }

        public String getLicenseDeclared() {
            return licenseDeclared;
        }

        public void setLicenseDeclared(
                String licenseDeclared) {

            this.licenseDeclared =
                    licenseDeclared;
        }

        public String getCopyrightText() {
            return copyrightText;
        }

        public void setCopyrightText(
                String copyrightText) {

            this.copyrightText =
                    copyrightText;
        }
    }

    // =========================================================
    // ExternalRef
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExternalRef {

        @NotBlank(message =
                "externalRef.comment is mandatory")
        private String comment;

        @NotBlank(message =
                "externalRef.referenceCategory is mandatory")
        private String referenceCategory;

        @NotBlank(message =
                "externalRef.referenceLocator is mandatory")
        private String referenceLocator;

        @NotBlank(message =
                "externalRef.referenceType is mandatory")
        private String referenceType;

        // =====================================================
        // Getter Setter
        // =====================================================

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getReferenceCategory() {
            return referenceCategory;
        }

        public void setReferenceCategory(
                String referenceCategory) {

            this.referenceCategory =
                    referenceCategory;
        }

        public String getReferenceLocator() {
            return referenceLocator;
        }

        public void setReferenceLocator(
                String referenceLocator) {

            this.referenceLocator =
                    referenceLocator;
        }

        public String getReferenceType() {
            return referenceType;
        }

        public void setReferenceType(
                String referenceType) {

            this.referenceType =
                    referenceType;
        }
    }
}