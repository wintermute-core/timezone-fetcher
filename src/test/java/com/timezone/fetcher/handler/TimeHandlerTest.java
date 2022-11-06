package com.timezone.fetcher.handler;

import com.timezone.fetcher.service.TimeZoneDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ratpack.core.handling.Context;
import ratpack.core.handling.internal.DefaultContext;

@ExtendWith(MockitoExtension.class)
public class TimeHandlerTest {

    @Mock
    TimeZoneDbClient timeZoneDbClient;



    @Test
    public void validRequest() {

        TimeHandler timeHandler = new TimeHandler(timeZoneDbClient);



    }

}
