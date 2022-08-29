package de.oryfox.foxshare.foxshareserver;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class Controller {

    private final List<Client> clients;
    private final Timer timer;

    public Controller() {
        clients = new LinkedList<>();
        timer = new Timer();
    }

    @GetMapping
    public Stream<Client> getClients(@RequestHeader("x-real-ip") String realIp,
                                     @RequestParam(required = false) Boolean allowed) {
        return clients.stream().filter(c -> realIp.equals(c.getExtIp()) && (allowed == null || c.isAllowed() == allowed));
    }

    @PatchMapping
    public void setAllowed(@RequestHeader("x-real-ip") String realIp,
                           @RequestParam String localIp,
                           @RequestParam(required = false, defaultValue = "false") boolean allowed) {
        clients.stream().filter(c -> realIp.equals(c.getExtIp()) && localIp.equals(c.getLocalIp())).findFirst().ifPresent(c -> c.setAllowed(allowed));
    }

    @PostMapping
    public void postClient(@RequestHeader("x-real-ip") String realIp,
                           @RequestParam String hostname,
                           @RequestParam String localIp,
                           @RequestParam(required = false, defaultValue = "false") boolean allowed) {
        clients.removeIf(c -> c.removal(realIp, localIp));
        var c = new Client(realIp, hostname, localIp, allowed);
        c.setTask(new TimerTask() {
            @Override
            public void run() {
                clients.remove(c);
            }
        });
        clients.add(c);

        timer.schedule(c.getTask(), Date.from(LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant()));
    }

    @DeleteMapping
    public void deleteClient(@RequestHeader("x-real-ip") String realIp,
                             @RequestParam String localIp) {
        clients.removeIf(c -> c.removal(realIp, localIp));
    }
}
