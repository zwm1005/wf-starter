package fun.werfamily.core.util.handler.log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import fun.werfamily.base.exception.WfBaseException;
import fun.werfamily.base.response.IResponseCode;
import fun.werfamily.base.response.Result;
import fun.werfamily.base.response.SysResponseCode;
import fun.werfamily.base.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/3/10.
 */
@Component("DefaultAopLogHandler")
@Slf4j
public class DefaultAopLogHandler extends BaseAopLogHandler<Result<?>> {

    @Override
    Result<?> buildMethodArgumentNotValidException(MethodArgumentNotValidException e, String traceId) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        List<String> results = new ArrayList<>(errors.size());
        if (!CollectionUtils.isEmpty(errors)) {
            results = errors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        }
        String[] message = results.toArray(new String[0]);
        return Result.error(SysResponseCode.ERROR_PARAM.code(), Arrays.toString(message), traceId);
    }

    @Override
    Result<?> buildWfBaseExceptionResult(WfBaseException e, String traceId) {
        IResponseCode responseCode = e.getCode();
        String friendlyMsg = ExceptionUtil.renderString(responseCode.friendlyMsg(), e.getMsgParams());
        return Result.error(responseCode.code(), friendlyMsg, traceId);
    }

    @Override
    Result<?> buildValidationExceptionResult(ValidationException e, String traceId) {
        return Result.error(SysResponseCode.ERROR_PARAM.code(), e.getMessage(), traceId);
    }

    @Override
    Result<?> buildInvalidFormatExceptionResult(InvalidFormatException e, String traceId) {
        return Result.error(SysResponseCode.ERROR_PARAM.code(), SysResponseCode.ERROR_PARAM.friendlyMsg(), traceId);
    }

    @Override
    Result<?> buildJsonParseExceptionResult(JsonParseException e, String traceId) {
        return Result.error(SysResponseCode.ERROR_PARAM.code(), SysResponseCode.ERROR_PARAM.friendlyMsg(), traceId);
    }

    @Override
    Result<?> buildMissingServletRequestParameterExceptionResult(MissingServletRequestParameterException e, String traceId) {
        return Result.error(SysResponseCode.ERROR_PARAM.code(), SysResponseCode.ERROR_PARAM.friendlyMsg(), traceId);
    }

    @Override
    Result<?> buildHttpMessageNotReadableExceptionResult(HttpMessageNotReadableException e, String traceId) {
        return Result.error(SysResponseCode.ERROR_PARAM.code(), SysResponseCode.ERROR_PARAM.friendlyMsg(), traceId);
    }

    @Override
    Result<?> buildExceptionResult(SysResponseCode sysResponseCode, String traceId) {
        return Result.error(SysResponseCode.SERVER_ERROR, traceId);
    }
}
