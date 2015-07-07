package model;

import java.util.List;

/**
 * Created by TEBAN on 07/07/2015.
 */
public class ProspectResponse
{
    public List<Prospect> getProspects() {
        return Prospects;
    }

    public void setProspects(List<Prospect> prospects) {
        Prospects = prospects;
    }

    private List<Prospect> Prospects;
}
