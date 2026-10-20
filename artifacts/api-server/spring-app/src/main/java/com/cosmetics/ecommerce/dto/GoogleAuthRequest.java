package com.cosmetics.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;


    public class GoogleAuthRequest {

        @NotBlank(message = "Google credential is required")
        private String credential;

        public GoogleAuthRequest() {
        }

        public String getCredential() {
            return credential;
        }

        public void setCredential(String credential) {
            this.credential = credential;
        }
}

