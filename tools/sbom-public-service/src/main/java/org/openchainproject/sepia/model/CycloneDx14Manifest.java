package org.openchainproject.sepia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CycloneDx14Manifest {

    @Valid
    @NotNull(message = "metadata is mandatory")
    private Metadata metadata;

    // =========================================================
    // Getter Setter
    // =========================================================

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
        @NotNull(message = "component is mandatory")
        private Component component;

        @Valid
        @NotNull(message = "supplier is mandatory")
        private Supplier supplier;

        // Getter Setter

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
    // Component
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Component {

        @Valid
        @NotEmpty(message = "component.licenses is mandatory")
        private List<LicenseWrapper> licenses;

        @NotBlank(message = "component.name is mandatory")
        private String name;

        @NotBlank(message = "component.group is mandatory")
        private String group;

        @NotBlank(message = "component.type is mandatory")
        private String type;

        @NotBlank(message = "component.version is mandatory")
        private String version;

        // =====================================================
        // Getter Setter
        // =====================================================

        public List<LicenseWrapper> getLicenses() {
            return licenses;
        }

        public void setLicenses(List<LicenseWrapper> licenses) {
            this.licenses = licenses;
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
    // Supplier
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Supplier {

        @Valid
        @NotEmpty(message = "supplier.contact is mandatory")
        private List<Contact> contact;

        @NotBlank(message = "supplier.name is mandatory")
        private String name;

        // Getter Setter

        public List<Contact> getContact() {
            return contact;
        }

        public void setContact(List<Contact> contact) {
            this.contact = contact;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // =========================================================
    // Contact
    // =========================================================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Contact {

        @Email(message = "supplier.contact.email must be valid")
        @NotBlank(message = "supplier.contact.email is mandatory")
        private String email;

        // Getter Setter

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}