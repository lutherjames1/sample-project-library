package eu.lucubratory.topro1.front.res.page;

import javax.annotation.ManagedBean;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.view.Viewable;

import eu.lucubratory.topro1.domain.persistence.entity.ProposalEntity;
import eu.lucubratory.topro1.domain.topic.TopicFacade;

import eu.lucubratory.topro1.front.common.json.RawJsonMapper;
import eu.lucubratory.topro1.front.dpo.ProposalType;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;


/**
 * Main page REST resource controller.
 */
@Path("/res/pages")
@Produces(MediaType.TEXT_HTML)
@ManagedBean
public class MainPageResource {

    private static Logger log=LoggerFactory.getLogger(MainPageResource.class);

    @Inject TopicFacade topicFacade;

    @Inject @RawJsonMapper ObjectMapper rawJsonMapper;


    /**
     *
     */
    @GET
    @Path("main")
    public Viewable index(@Context HttpServletRequest request) {

    	log.info("MAIN page: IP {}, GET.",request.getRemoteAddr());

        // Handle eager topic list and reference data generation by JSP templating
        prepareData(request);

        return new Viewable("/main.jsp", null);
    }


    /**
     *
     */
    private void prepareData(HttpServletRequest request) {

        List<ProposalEntity> proposals=topicFacade.getProposals();
        List<ProposalType> proposalTypes=ProposalType.init();

        String jsonProposals=null;
        String jsonProposalTypes=null;
        try {
            jsonProposals=rawJsonMapper.writeValueAsString(proposals);
            jsonProposalTypes=rawJsonMapper.writeValueAsString(proposalTypes);
        }
        catch (IOException  ex) {
            log.error("Serialising data",ex);
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        }

        log.debug("Return JSON: {}",jsonProposals);
        log.debug("Return JSON: {}",jsonProposalTypes);

        request.setAttribute("topics",jsonProposals);
        request.setAttribute("proposalTypes",jsonProposalTypes);
    }

}

