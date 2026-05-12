package com.rakshakavach.domain.repository

import com.rakshakavach.data.local.IncidentDao
import com.rakshakavach.data.local.IncidentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IncidentRepository @Inject constructor(
    private val incidentDao: IncidentDao
) {
    fun getAllIncidents(): Flow<List<IncidentEntity>> = incidentDao.getAllIncidents()
    
    suspend fun insertIncident(incident: IncidentEntity) {
        incidentDao.insertIncident(incident)
    }
}
