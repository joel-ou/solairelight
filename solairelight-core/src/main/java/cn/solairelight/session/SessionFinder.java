package cn.solairelight.session;

import cn.solairelight.exception.ExceptionEnum;
import cn.solairelight.exception.ResponseMessageException;
import cn.solairelight.expression.ExpressionEvaluator;
import cn.solairelight.expression.SpringExpressionEvaluator;
import cn.solairelight.session.index.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 实现思路：
 * <li>  查找Session需要两个参数：Range 、 Filter</li>
 * <li>  首先根据 Range 参数决定Session的查找范围，然后再根据 Filter 对这些Session进行过滤。</li>
 * @author Joel Ou
 */
@Slf4j
@Component
public class SessionFinder {
    @Resource
    private IndexService indexService;

    /**
     * @param rangeList session finding range
     * @param predicate session feature predicate
     * @throws ResponseMessageException
     */
    public Collection<BasicSession> finding(LinkedList<String[]> rangeList, String predicate) throws ResponseMessageException{
        if(rangeList == null){
            return SessionBroker.getStorage().getAll();
        }
        //get sessions by range
        Collection<BasicSession> sessions = new LinkedHashSet<>();
        for (String[] kv : rangeList) {
            if(kv.length != 2){
                throw new ResponseMessageException(ExceptionEnum.INVALID_RANGE_VALUE, Arrays.toString(kv));
            }
            Set<String> indexes = indexService.getAll(kv[0], kv[1]);
            if(CollectionUtils.isEmpty(indexes)) continue;
            sessions.addAll(SessionBroker.getStorage().getAll(indexes));
        }

        if(!StringUtils.hasText(predicate)){
            return sessions;
        }
        try {
            //filter sessions by predicate
            ExpressionEvaluator<Object> evaluator = new SpringExpressionEvaluator<>();
            sessions = sessions
                    .stream()
                    .parallel()
                    .filter(session->evaluator.evaluate(predicate, session.getUserMetadata().getUserFeatures()))
                    .collect(Collectors.toSet());
            log.info("predicate matched {}.", sessions.size());
            return sessions;
        } catch (SpelEvaluationException e) {
            if(e.getMessageCode() == SpelMessage.SETVALUE_NOT_SUPPORTED
                    || e.getMessageCode() == SpelMessage.PROPERTY_OR_FIELD_NOT_WRITABLE){
                throw new ResponseMessageException(ExceptionEnum.OPERATION_NOT_SUPPORTED);
            }
            log.error("parse expression error.", e);
            throw new ResponseMessageException("parsing expression failed. ");
        }
    }
}
