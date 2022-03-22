package com.adil

import aws.sdk.kotlin.runtime.auth.credentials.Credentials
import aws.sdk.kotlin.runtime.auth.credentials.CredentialsProvider
import aws.sdk.kotlin.runtime.client.AwsClientConfig
import com.adil.utils.Constants
import com.adil.utils.Constants.ACCESS_KEY_ID
import com.adil.utils.Constants.SECRET_ACCESS_KEY

data class AwsConfig(override val credentialsProvider: CredentialsProvider = CredentialProvider(), override val region: String = Constants.EU_WEST_REGION) : AwsClientConfig

class CredentialProvider : CredentialsProvider {
    override suspend fun getCredentials(): Credentials {
        return Credentials(System.getenv(ACCESS_KEY_ID), System.getenv(SECRET_ACCESS_KEY))
    }
}