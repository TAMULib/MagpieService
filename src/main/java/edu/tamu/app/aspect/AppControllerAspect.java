/* 
 * AppControllerAspect.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import edu.tamu.framework.aspect.CoreControllerAspect;

/**
 * Application Controller Aspect
 * 
 * @author
 *
 */
@Component
@Aspect
public class AppControllerAspect extends CoreControllerAspect {

}
