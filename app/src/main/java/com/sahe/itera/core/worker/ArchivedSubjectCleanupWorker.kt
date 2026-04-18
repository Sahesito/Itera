package com.sahe.itera.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.sahe.itera.domain.usecase.subject.DeleteExpiredArchivedUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class ArchivedSubjectCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val deleteExpiredArchived: DeleteExpiredArchivedUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        deleteExpiredArchived()
        return Result.success()
    }

    companion object {
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<ArchivedSubjectCleanupWorker>(
                1, TimeUnit.DAYS
            ).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "archived_cleanup",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}