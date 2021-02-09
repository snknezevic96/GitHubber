package com.futuradev.githubber.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class User(
    @PrimaryKey
    var id: Int = 0,
    var avatar_url: String?= null,
    var blog: String?= null,
    var created_at: String?= null,
    var events_url: String?= null,
    var followers: Int?= null,
    var followers_url: String?= null,
    var following: Int?= null,
    var following_url: String?= null,
    var gists_url: String?= null,
    var gravatar_id: String?= null,
    var html_url: String?= null,
    var login: String?= null,
    var node_id: String?= null,
    var organizations_url: String?= null,
    var public_gists: Int?= null,
    var public_repos: Int?= null,
    var received_events_url: String?= null,
    var repos_url: String?= null,
    var site_admin: Boolean?= null,
    var starred_url: String?= null,
    var subscriptions_url: String?= null,
    var type: String?= null,
    var updated_at: String?= null,
    var url: String? = null,
    var name : String? = null,
    var company : String? = null,
    var location : String? = null,
    var email : String? = null,
    var hireable : String? = null,
    var bio : String? = null,
    var twitter_username: String? = null
)