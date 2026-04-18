package com.sahe.itera.domain.usecase.subject

import com.sahe.itera.domain.repository.SubjectRepository
import javax.inject.Inject

class DeleteExpiredArchivedUseCase @Inject constructor(private val repo: SubjectRepository) {
    suspend operator fun invoke() {
        val cutoff = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        repo.deleteExpiredArchived(cutoff)
    }
}