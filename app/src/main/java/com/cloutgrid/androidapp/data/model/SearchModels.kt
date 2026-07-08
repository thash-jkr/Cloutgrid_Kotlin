package com.cloutgrid.androidapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AllUsersResponse(
    val creators: List<UserContainer>,
    val businesses: List<UserContainer>
)