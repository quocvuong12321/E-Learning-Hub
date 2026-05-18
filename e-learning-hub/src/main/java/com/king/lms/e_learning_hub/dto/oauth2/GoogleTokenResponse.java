package com.king.lms.e_learning_hub.dto.oauth2;

import com.google.auto.value.AutoValue.Builder;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleTokenResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("expires_in")
    private Integer expiresIn;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("id_token")
    private String idToken;

    @SerializedName("scope")
    private String scope;

    @SerializedName("error")
    private String error;

    @SerializedName("error_description")
    private String errorDescription;
}
