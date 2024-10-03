package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static javafx.collections.FXCollections.*;

public final class AircraftStateManager {
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> association = new HashMap<>();
    private final ObservableSet<ObservableAircraftState> set ;
    private final AircraftDatabase dataBase;
    private long currentTime;



    public AircraftStateManager(AircraftDatabase data) {
        this.dataBase = data;
        set = observableSet();
        ObservableSet<ObservableAircraftState> states = unmodifiableObservableSet(set);
    }


    public ObservableSet<ObservableAircraftState> states() {
        return unmodifiableObservableSet(set);
    }

    public void updateWithMessage(Message message) throws IOException {
        currentTime = message.timeStampNs();
        IcaoAddress key = message.icaoAddress();
        if (association.containsKey(key)) {
            association.get(key).update(message);
            if (association.get(key).stateSetter().getPosition() != null) {
                set.add(association.get(key).stateSetter());
            }
        } else {
            association.put(key, new AircraftStateAccumulator<>(new ObservableAircraftState(key, dataBase.get(key))));
        }
    }


    public void purge() {
        Iterator<AircraftStateAccumulator<ObservableAircraftState>> iterator = (association.values()).iterator();
        while (iterator.hasNext()) {
            AircraftStateAccumulator<ObservableAircraftState> state = iterator.next();
            if (currentTime - (state.stateSetter()).getLastMessageTimeStampNs() > (long)1E9 * Units.Time.MINUTE) {
                iterator.remove();
                set.remove(state.stateSetter());
            }
        }
    }
}
