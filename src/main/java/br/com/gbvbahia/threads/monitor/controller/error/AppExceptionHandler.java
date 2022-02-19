package br.com.gbvbahia.threads.monitor.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import br.com.gbvbahia.threads.monitor.controller.Pages;
import lombok.extern.slf4j.Slf4j;

/**
 * Project: Ecommerce
 *
 * @author Guilherme
 * @version 1.0
 * @since 15/04/18
 */
@ControllerAdvice
@Slf4j
public class AppExceptionHandler {

   
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleInternalServerError(Exception exception) {
    		//DefaultHandlerExceptionResolver
    	//ExceptionHandlerExceptionResolver
        log.error(exception.getMessage());
        log.error("Exception:", exception);

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName(Pages.INTERNAL_ERROR_500.pageName);
        modelAndView.addObject("exception", exception);

        return modelAndView;
    }
}