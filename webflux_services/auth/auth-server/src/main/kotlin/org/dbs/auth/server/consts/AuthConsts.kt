package org.dbs.auth.server.consts

object AuthConsts {
    object Packages {
        const val ALL_PACKAGES = "org.dbs.auth.server"
        const val REPOSITORY_PACKAGE = ALL_PACKAGES + ".repo"
    }

    object Sequences {
        const val SEQ_CARDS = "seq_tnk_card_id"
        const val SEQ_SERVER = "seq_tkn_servers"
        const val SEQ_REQUESTS = "seq_tkn_requests"
        const val SEQ_IP_BANS = "seq_tkn_ip_ban"
    }

    object Consumers {
        const val HTTP_PRODUCER_ID = "http-request"
    }

    // Caches
    object Caches {
        const val CACHE_APPLICATION = "applicationsRef"
        const val CACHE_SERVER_STATUS = "serversStatusesRef"
        const val CACHE_SERVER_EVENTS = "serversEventsRef"
    }
}
