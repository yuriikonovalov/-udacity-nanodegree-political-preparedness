package com.example.android.politicalpreparedness.data

interface ElectionsRepository {
    suspend fun refreshElections()
}