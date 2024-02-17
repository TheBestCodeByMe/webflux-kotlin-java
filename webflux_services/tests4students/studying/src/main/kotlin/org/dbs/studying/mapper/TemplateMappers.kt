package org.dbs.quiz.mapper

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toLong
import org.dbs.entity.core.AbstractEntityExtension.assignCoreEntity
import org.dbs.entity.core.AbstractEntityExtension.getCoreEntity
import org.dbs.protobuf.quiz.CreateOrUpdateTemplateRequest
import org.dbs.quiz.dto.template.QuizTemplateHistItemDb
import org.dbs.quiz.dto.template.QuizTemplateItem
import org.dbs.quiz.enums.TemplateGroupEnum
import org.dbs.quiz.model.Template
import org.dbs.quiz.model.hist.TemplateHist
import org.springframework.stereotype.Service
import org.dbs.protobuf.quiz.QuizTemplateItem4HistList as ITEM_HIST
import org.dbs.protobuf.quiz.QuizTemplateItem4List as ITEM

@Service
class TemplateMappers : Logging {
    fun templateItem2GrpcItem(src: QuizTemplateItem): ITEM =
        ITEM.newBuilder()
            .setTemplateCode(src.templateCode)
            .setName(src.name)
            .setGroup(src.group.getGroupCode())
            .setTimeOut(src.quizTimeout)
            .build()

    fun templateItem2GrpcItemHist(src: QuizTemplateHistItemDb): ITEM_HIST =
        ITEM_HIST.newBuilder()
            .setTemplateCode(src.templateCode)
            .setName(src.name)
            .setGroup(src.groupId.getGroupCode())
            .setTimeOut(src.quizTimeout)
            .setModifiedTime(src.actualDate.toLong())
            .build()

    fun createHist(src: Template) = TemplateHist(
            actualDate = src.getCoreEntity().modifyDate,
            templateId = src.templateId,
            templateCode = src.templateCode,
            name = src.name,
            group = src.group,
            body = src.body,
            timeout = src.timeOut,
            hash = src.hash
        )

    fun updateTemplate(src: Template, srcDto: CreateOrUpdateTemplateRequest): Template =
        src.copy(
            templateCode = srcDto.templateCode,
            name = srcDto.name,
            group = TemplateGroupEnum.getEnum(srcDto.group),
            body = srcDto.body,
            timeOut = srcDto.timeOut,
            hash = srcDto.body.trim().hashCode()
        ).also { it.assignCoreEntity(src) }

}
