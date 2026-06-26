package com.cloutgrid.androidapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID

@Serializable
data class UserContainer(
    @SerialName("user") var profile: UserProfile,
    val area: String? = null,
    @SerialName("instagram_connected") var instagramConnected: Boolean? = null,
    @SerialName("youtube_connected") var youtubeConnected: Boolean? = null,
    @SerialName("target_audience") val targetAudience: String? = null,
    val website: String? = null,
    @SerialName("is_following") var isFollowing: Boolean? = null,
    @SerialName("is_blocking") var isBlocking: Boolean? = null,
    @SerialName("is_blocker") var isBlocker: Boolean? = null
) {
    @Transient
    val id: String = UUID.randomUUID().toString()
}

@Serializable
data class UserProfile(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val bio: String? = null,
    @SerialName("user_type") val userType: String,
    @SerialName("profile_photo") val profilePhoto: String,
    @SerialName("followers_count") var followersCount: Int,
    @SerialName("following_count") var followingCount: Int
)

@Serializable
data class LoginResponse(
    val user: UserContainer,
    val access: String,
    val refresh: String
)

@Serializable
class EmptyResponse