package com.neuroisp.service;

import com.neuroisp.dto.HotspotActiveClient;
import com.neuroisp.dto.PppoeActiveClient;
import com.neuroisp.entity.MikrotikRouter;
import lombok.RequiredArgsConstructor;
import me.legrange.mikrotik.ApiConnection;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor

public class MikrotikActiveClientService {

    private final MikrotikService mikrotikService;
    private final ActiveRouterResolverService routerResolver;


    public List<HotspotActiveClient> getActiveHotspotUsers() {

        MikrotikRouter router = routerResolver.getActiveRouter();

        try (ApiConnection con = mikrotikService.connect(router)) {
            var rows = con.execute("/ip/hotspot/active/print");

            return rows.stream()
                    .map(r -> new HotspotActiveClient(
                            r.get("user"),
                            r.get("address"),
                            r.get("mac-address"),
                            r.get("uptime"),
                            r.get("server")
                    ))
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch active hotspot users", e);
        }
    }

    public List<PppoeActiveClient> getActivePppoeUsers() {

        MikrotikRouter router = routerResolver.getActiveRouter();

        try (ApiConnection con = mikrotikService.connect(router)) {
            var rows = con.execute("/ppp/active/print");

            return rows.stream()
                    .map(r -> new PppoeActiveClient(
                            r.get("name"),
                            r.get("address"),
                            r.get("uptime"),
                            r.get("service")
                    ))
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch active PPPoE users", e);
        }
    }

    public int getActiveHotspotUserCount() {

        MikrotikRouter router = routerResolver.getActiveRouter();

        try (ApiConnection con = mikrotikService.connect(router)) {
            return con.execute("/ip/hotspot/active/print").size();
        } catch (Exception e) {
            throw new RuntimeException("Failed to count active hotspot users", e);
        }
    }

    public int getActivePppoeUserCount() {

        MikrotikRouter router = routerResolver.getActiveRouter();

        try (ApiConnection con = mikrotikService.connect(router)) {
            return con.execute("/ppp/active/print").size();
        } catch (Exception e) {
            throw new RuntimeException("Failed to count active PPPoE users", e);
        }
    }

    public int getTotalActiveUsers() {
        return getActiveHotspotUserCount() + getActivePppoeUserCount();
    }

}
