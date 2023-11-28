package com.lmig.corporate.ignite2023submissions.entities;
// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */

import java.util.ArrayList;

public class ChatResponse{
    public String id;
    public String object;
    public int created;
    public String model;
    public ArrayList<PromptFilterResult> prompt_filter_results;
    public ArrayList<Choice> choices;
    public Usage usage;
}
