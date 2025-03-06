package org.source.transaction.management.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.source.spring.exception.BizException;
import org.source.spring.i18n.annotation.I18nDict;
import org.source.spring.i18n.annotation.I18nRef;
import org.source.spring.i18n.enums.I18nRefTypeEnum;
import org.source.utility.exceptions.EnumProcessor;

@I18nDict(value = @I18nRef(type = I18nRefTypeEnum.FIELD, value = "message"))
@Getter
@AllArgsConstructor
public enum BusinessExceptionEnum implements EnumProcessor<BizException> {
    /**
     * 数据库记录不存在
     */
    RECORD_NOT_FOUND("记录不存在"),
    RECORD_HAS_EXISTS("记录已存在"),
    ;

    private final String message;

    @Override
    public String getCode() {
        return name();
    }

}
