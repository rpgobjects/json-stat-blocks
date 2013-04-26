<?php
/**
 * This simple script converts my google spreadsheet npc (exported as txt) into a json stat blocks
 * 
 */


$file_name ="npc.txt";

function parseTxt($txt) {
    $headers = array("detection","defenses","offensive","statistics","skills","spells");
    $exploders = array("class","feats","defensive_abilities","senses","melee","ranged","spell_like_abilities","languages","combat_gear","other_gear","sq");
    $json_ar = array("stat_block_title"=>"Pathfinder Stat Block","stat_block_name"=>"pathfinder.npc.standard","stat_block_version"=>"1");
    $lines = explode("\n", $txt);
    $object_k = false;
    while (list($key, $l)  = each($lines)) {
        $cells = explode("\t", $l);
        $key = str_replace(array(" ","."), array("_",""), strtolower(trim($cells[0])));
        unset($cells[0]);
        $val = trim(implode(" ", $cells));
        if(in_array($key,$headers)) {
            $object_k = $key;
            $json_ar[$key] = array();
            continue;
        }
        if(in_array($key,$exploders)) {
            $val = explode(",", $val);
            $val = array_map("trim", $val);
        }
        
        if($key=="class" && $val =="0") {
            continue;
        }
        
        if($object_k)
            $json_ar[$object_k][$key] = $val;
        else
            $json_ar[$key] = $val;
    }
    header( "Content-type: application/json" );
    echo json_encode($json_ar);
}

parseTxt(file_get_contents("npc.txt"));

?>