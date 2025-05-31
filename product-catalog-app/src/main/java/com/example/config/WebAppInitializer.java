//package com.example.config;
//
//import jakarta.servlet.MultipartConfigElement;
//import jakarta.servlet.ServletRegistration;
//import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
//
//public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
//
//    private static final String TMP_FOLDER = "/tmp";
//    private static final int MAX_UPLOAD_SIZE = 5 * 1024 * 1024;
//
//    @Override
//    protected Class<?>[] getRootConfigClasses() {
//        return null;
//    }
//
//    @Override
//    protected Class<?>[] getServletConfigClasses() {
//        return new Class[] {WebConfig.class};
//    }
//
//    @Override
//    protected String[] getServletMappings() {
//        return new String[] {"/"};
//    }
//
//    @Override
//    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
//        registration.setMultipartConfig(
//                new MultipartConfigElement(
//                        TMP_FOLDER,
//                        MAX_UPLOAD_SIZE,
//                        MAX_UPLOAD_SIZE * 2,
//                        MAX_UPLOAD_SIZE / 2
//                )
//        );
//    }
//}
