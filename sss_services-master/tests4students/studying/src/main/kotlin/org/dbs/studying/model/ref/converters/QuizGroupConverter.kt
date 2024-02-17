package org.dbs.quiz.model.ref.converters

import org.dbs.quiz.enums.TemplateGroupEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class QuizGroupConverter : Converter<Int, TemplateGroupEnum> {
    override fun convert(id: Int): TemplateGroupEnum = TemplateGroupEnum.getEnum(id)
}
