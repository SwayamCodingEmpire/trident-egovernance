package com.trident.egovernance.global.nav;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
public class MenuServices {
    private final ObjectMapper objectMapper;
    private final MenuJsonLoadingService menuJsonLoadingService;
    private final Logger logger = LoggerFactory.getLogger(MenuServices.class);

    public MenuServices(ObjectMapper objectMapper, MenuJsonLoadingService menuJsonLoadingService) {
        this.objectMapper = objectMapper;
        this.menuJsonLoadingService = menuJsonLoadingService;
    }

    public void saveMenu(MenuNode menuNode) {
        File file = new File("src/main/resources/menu.json");
        try {
            // Create the file if it does not exist
            if (!file.exists()) {
                logger.info("Creating file");
                file.createNewFile();
            }

            // Write the MenuNode to the file
            ObjectMapper objectMapper = new ObjectMapper();
            logger.info("Saving menu");
            String json = objectMapper.writeValueAsString(menuNode);
            logger.info("Writing menu");
            try (FileWriter writer = new FileWriter(file)) {
                logger.info("Writing menu next");
                writer.write(json);
                logger.info("Writing menu last");
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

    public boolean addNode(MenuNodeDto menuNodeDto) {
        // Create the file if it does not exist
        try{
            File file = new File("src/main/resources/menu.json");
            if (file.exists()) {
                MenuNode menuNode = objectMapper.readValue(file, MenuNode.class);
                MenuNode temp = menuNode;
                String url = menuNodeDto.getParentUrl();
                String[] brokenUrls = url.split("/");
                logger.info(brokenUrls[0]);
                logger.info(brokenUrls[1]);
                String currUrl = temp.getUrl();
                logger.info(currUrl);
                if(!(brokenUrls[0].equals(currUrl))){
                    throw new RuntimeException("Invalid URL");
                }
                for (int k = 1;k<brokenUrls.length; k++) {
//                    if(brokenUrl.isEmpty()){
//                        continue;
//                    }
                    boolean found = false;
                    for (MenuNode node : temp.getChildren()) {
                        logger.info(brokenUrls[k]);
                        logger.info(node.toString());
                        if(node.getUrl().equals(brokenUrls[k])){
                            logger.info(node.toString());
                            logger.info("FOund is true");
                            found = true;
                            temp = node;
                            break;
                        }
                    }
                    if (!found){
                        logger.info("Its false");
                        return found;
                    }
                }
                Map<String, Set<String>> titleAndRoles = menuNodeDto.getTitles();

                List<String> urls = menuNodeDto.getUrls();
                int i = 0;
                for(Map.Entry<String,Set<String>> entry : titleAndRoles.entrySet()){
                    String titles = entry.getKey();
                    Set<String> roles = entry.getValue();
                    MenuNode temp1 = new MenuNode();
                    String thisUrl = urls.get(i);
                    temp1.setUrl(thisUrl);
                    temp1.setTitle(titles);
                    temp1.setAccessRoles(roles);
                    temp1.setChildren(new ArrayList<>());
                    temp.addChild(temp1);
                    i++;

                }
                objectMapper.writeValue(file, menuNode);
                return true;
            }
        }catch (IOException e){
            logger.error(e.getMessage());
            return false;
        }
        return false;
    }

    public AllowedUrls getAllUrls(String accessRole){
        List<String> urls = new ArrayList<>();
        try{
            MenuNode root = menuJsonLoadingService.loadJson();
            Deque<Pair<MenuNode,StringBuilder>> stack  = new ArrayDeque<>();
            stack.push(Pair.of(root, new StringBuilder()));
            while(!stack.isEmpty()){
                Pair<MenuNode,StringBuilder> pair = stack.pop();
                MenuNode currNode = pair.getFirst();
                StringBuilder ancestorNames = pair.getSecond();
                StringBuilder newAncestorNames = new StringBuilder(ancestorNames);
                if(newAncestorNames.length()>0){
                    newAncestorNames.append("/");
                }
                newAncestorNames.append(currNode.getUrl());
                for(String role : currNode.getAccessRoles()){
                    if(role.compareTo(accessRole)==0){
                        urls.add(newAncestorNames.toString());
                    }
                }
                for(int i = currNode.getChildren().size()-1;i>=0;i--){
                    stack.push(Pair.of(currNode.getChildren().get(i),new StringBuilder(newAncestorNames)));
                }
            }
            AllowedUrls allowedUrls = new AllowedUrls();
            allowedUrls.setUrls(urls);
            return allowedUrls;
        }catch (IOException e){
            logger.error(e.getMessage());
            return null;
        }
    }

}
