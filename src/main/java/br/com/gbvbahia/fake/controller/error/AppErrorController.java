package br.com.gbvbahia.fake.controller.error;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import br.com.gbvbahia.fake.controller.Pages;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class AppErrorController implements ErrorController {

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request, Exception exception) {

        Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        ModelAndView modelAndView = new ModelAndView();


        switch (status) {
            case 400: { //"Http Error Code: 400. Bad Request"
                break;
            }
            case 401: { //"Http Error Code: 401. Unauthorized"
                break;
            }
            case 404: { //"Http Error Code: 404. Resource not found"
                log.warn("Handling: HttpStatus.NOT_FOUND", exception);
                log.warn("request: {}", request.getContextPath());
                modelAndView.setViewName(Pages.NOT_FOUND_404.pageName);
                modelAndView.addObject("exception", exception);
                return modelAndView;
            }

            case 500:
            default: { // Http Error Code: 500. Internal Server Error
                log.warn("Handling: {}", status);
                log.error("Handling: HttpStatus.INTERNAL_SERVER_ERROR", exception);
                modelAndView.setViewName(Pages.INTERNAL_ERROR_500.pageName);
                modelAndView.addObject("exception", exception);
                return modelAndView;
            }
        }


        return modelAndView;
    }

    public String getErrorPath() {
        return "/error";
    }

}