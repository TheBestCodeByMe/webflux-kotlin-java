package org.dbs.media.exception

import org.dbs.application.core.exception.InternalAppException
import org.dbs.consts.ErrMsg

class MultipartFileException(s: ErrMsg) : InternalAppException(s)
