package com.adil

import aws.sdk.kotlin.runtime.auth.credentials.Credentials
import aws.sdk.kotlin.runtime.auth.credentials.CredentialsProvider
import aws.sdk.kotlin.runtime.client.AwsClientConfig

data class AwsConfig(override val credentialsProvider: CredentialsProvider = CredentialProvider(), override val region: String = "eu-west-2") : AwsClientConfig

class CredentialProvider : CredentialsProvider {
    override suspend fun getCredentials(): Credentials {
        return Credentials("", "")
    }
}