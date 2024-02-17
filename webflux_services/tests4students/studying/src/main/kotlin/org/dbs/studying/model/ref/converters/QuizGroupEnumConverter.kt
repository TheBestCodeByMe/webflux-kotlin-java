package org.dbs.quiz.model.ref.converters

import org.dbs.quiz.enums.TemplateGroupEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class QuizGroupEnumConverter : Converter<TemplateGroupEnum, Int> {
    override fun convert(enum: TemplateGroupEnum): Int = enum.getCode()
}
