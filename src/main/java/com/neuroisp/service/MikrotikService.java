package com.neuroisp.service;

import com.neuroisp.dto.HotspotClient;
import com.neuroisp.entity.MikrotikRouter;
import lombok.RequiredArgsConstructor;
import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;
import org.springframework.stereotype.Service;

import javax.net.SocketFactory;

@Service
@RequiredArgsConstructor
public class MikrotikService {

    /**
     * Test connection to a Mikrotik router
     */
    public boolean testConnection(String ip, int port, String username, String password) {
        try (ApiConnection con = ApiConnection.connect(SocketFactory.getDefault(), ip, port, 5000)) {
            con.login(username, password);
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        } catch (MikrotikApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Connect to a Mikrotik router and return the ApiConnection object
     */
    public ApiConnection connect(MikrotikRouter router) throws MikrotikApiException {
        ApiConnection con = ApiConnection.connect(
                SocketFactory.getDefault(),
                router.getIpAddress(),
                router.getApiPort(),
                5000 // timeout in ms
        );
        con.login(router.getUsername(), router.getPassword());
        return con;

    }
    // ⭐ NEW METHOD
    public HotspotClient findActiveClientByIp(
            MikrotikRouter router,
            String clientIp
    ) {
        System.out.println(clientIp);
        try (ApiConnection con = connect(router)) {

            var active = con.execute("/ip/hotspot/host/print");

            for (var entry : active) {
                if (clientIp.equals(entry.get("address"))) {
                    return new HotspotClient(
                            entry.get("mac-address"),
                            entry.get("address"),
                            entry.get("server")
                    );
                }
            }

            throw new RuntimeException("Client not active in hotspot");

        } catch (Exception e) {
            throw new RuntimeException("MikroTik query failed", e);
        }
    }
}


