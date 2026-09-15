package org.openchainproject.sepia.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CDQCycloneDx16Manifest {

    @Valid
    @NotNull
    private Metadata metadata;

    // Getter Setter
    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    // =========================================================
    // Metadata
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
    	
    	@Valid
        @NotNull
        private List<Authors> authors;

        @Valid
        @NotNull
        private Manufacturer manufacturer;

        @Valid
        @NotNull
        private Component component;

        @Valid
        @NotNull
        private Supplier supplier;

        // Getter Setter
        
        // Getter Setter

        public List<Authors> getAuthors() {
            return authors;
        }

        public void setAuthors(List<Authors> authors) {
            this.authors = authors;
        }
        
        public Manufacturer getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(Manufacturer manufacturer) {
            this.manufacturer = manufacturer;
        }

        public Component getComponent() {
            return component;
        }

        public void setComponent(Component component) {
            this.component = component;
        }

        public Supplier getSupplier() {
            return supplier;
        }

        public void setSupplier(Supplier supplier) {
            this.supplier = supplier;
        }
    }

    // =========================================================
    // Manufacturer
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Manufacturer {

        @NotBlank(message = "manufacturer.name is mandatory")
        private String name;

        // Getter Setter
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    
 // =========================================================
    // Authors
    // =========================================================
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Authors {

        @NotBlank(message = "Authors.name is mandatory")
        private String name;

        // Getter Setter
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // =========================================================
    // Component
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Component {

        @Valid
        @NotEmpty(message = "component.licenses is mandatory")
        private List<LicenseWrapper> licenses;

        @Valid
        @NotEmpty(message = "component.externalReferences is mandatory")
        private List<ExternalReference> externalReferences;

        @NotBlank(message = "component.name is mandatory")
        private String name;

        @NotBlank(message = "component.group is mandatory")
        private String group;

        @NotBlank(message = "component.type is mandatory")
        private String type;

        @NotBlank(message = "component.version is mandatory")
        private String version;

        @NotBlank(message = "component.purl is mandatory")
        private String purl;

        // Getter Setter

        public List<LicenseWrapper> getLicenses() {
            return licenses;
        }

        public void setLicenses(List<LicenseWrapper> licenses) {
            this.licenses = licenses;
        }

        public List<ExternalReference> getExternalReferences() {
            return externalReferences;
        }

        public void setExternalReferences(
                List<ExternalReference> externalReferences) {
            this.externalReferences = externalReferences;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getPurl() {
            return purl;
        }

        public void setPurl(String purl) {
            this.purl = purl;
        }
    }

    // =========================================================
    // LicenseWrapper
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LicenseWrapper {

        @Valid
        @NotNull(message = "license object is mandatory")
        private License license;

        // Getter Setter
        public License getLicense() {
            return license;
        }

        public void setLicense(License license) {
            this.license = license;
        }
    }

    // =========================================================
    // License
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class License {

        @NotBlank(message = "license.id is mandatory")
        private String id;

        // Getter Setter
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    // =========================================================
    // ExternalReference
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExternalReference {

        @NotBlank(message = "externalReference.type is mandatory")
        private String type;

        @NotBlank(message = "externalReference.url is mandatory")
        private String url;

        // Getter Setter
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    // =========================================================
    // Supplier
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Supplier {

        @NotBlank(message = "supplier.name is mandatory")
        private String name;

        // Getter Setter
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}