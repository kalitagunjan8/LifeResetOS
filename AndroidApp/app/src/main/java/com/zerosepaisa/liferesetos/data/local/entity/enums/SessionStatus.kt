package com.zerosepaisa.liferesetos.data.local.entity.enums

/**
 * Only COMPLETED and ENDED_EARLY are produced in v0.5.0.
 * BROKEN is reserved for future interruption-detection work (ADR-012) so
 * that adding real detection later doesn't require a schema/enum migration.
 */
enum class SessionStatus {
    COMPLETED,
    ENDED_EARLY,
    BROKEN
}
