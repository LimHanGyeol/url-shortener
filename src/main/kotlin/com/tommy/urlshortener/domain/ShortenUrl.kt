package com.tommy.urlshortener.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table

@Entity
@Table(name = "shorten_url")
class ShortenUrl(

    val shortenKey: Long,

    @Lob
    @Column(columnDefinition = "TEXT", unique = true)
    val originUrl: String,

    @Column(unique = true)
    val hashedOriginUrl: String,

    val shortUrl: String,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShortenUrl

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "ShortenUrl(shortenKey=$shortenKey, originUrl='$originUrl', hashedOriginUrl='$hashedOriginUrl', shortUrl='$shortUrl', id=$id)"
    }
}
