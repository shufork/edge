package me.shufork.biz.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import me.shufork.common.constants.HttpHerderKeyConstants;
import me.shufork.common.constants.RedisKeyConstants;
import me.shufork.common.dto.session.UserSessionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ZuulHeaderHandler extends ZuulFilter {

    private static final String USER_ID = "user_id";

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    @Qualifier("commonRedisTemplate")
    private RedisTemplate redisTemplate;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2Authentication) {
            String loginName = (String) authentication.getPrincipal();
            // see auth-gateway
            Object obj = redisTemplate.opsForValue().get(String.format("%s:%s", RedisKeyConstants.LOGIN_NAME_TO_USER_SESSION, loginName));
            if (obj != null && obj instanceof UserSessionDto) {
                UserSessionDto userSessionDto = (UserSessionDto) obj;

                RequestContext ctx = RequestContext.getCurrentContext();
                ctx.set(USER_ID, userSessionDto.getId());
                return true;
            }
        }

        return false;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulRequestHeader(HttpHerderKeyConstants.X_USER_ID, (String) ctx.get(USER_ID));
        return null;
    }
}
