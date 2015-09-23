package sws.murcs.reporting.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter for converting a map of estimates to their dates to a representation in XML.
 */
public class EstimateMapAdapter extends XmlAdapter<EstimateXMLRepresentation[], Map<LocalDate, Float>> {
    @Override
    public Map<LocalDate, Float> unmarshal(final EstimateXMLRepresentation[] xmlRepresentations) throws Exception {
        Map<LocalDate, Float> map = new HashMap<>();
        for (EstimateXMLRepresentation representation : xmlRepresentations) {
            map.put(representation.date, representation.estimateValue);
        }
        return map;
    }

    @Override
    public EstimateXMLRepresentation[] marshal(final Map<LocalDate, Float> map) throws Exception {
        EstimateXMLRepresentation[] xmlRepresentations = new EstimateXMLRepresentation[map.size()];
        int i = 0;
        for (Map.Entry<LocalDate, Float> entry : map.entrySet()) {
            xmlRepresentations[i++] = new EstimateXMLRepresentation(entry.getKey(), entry.getValue());
        }
        return xmlRepresentations;
    }
}
