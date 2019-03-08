(* Types *)

type ingredient =
    | IceCream
    | BlueBerry
    | Unknown

type tile = 
    | Dish
    | Source of ingredient
    | Bell
    | Table
    | EmptyTile

type item =
    | Ingredient of ingredient
    | Dish of ingredient list
    | Nothing

type order = ingredient list * int
;;

(* Parsing functions *)
let print line = prerr_endline line; line in

let parseIngredient ingredient =  match ingredient with
    | "BLUEBERRIES" -> BlueBerry
    | "ICE_CREAM" -> IceCream
    | _ -> Unknown
in

let parseDish dishString = 
    String.split_on_char '-' dishString |>
    List.filter (fun s -> s <> "DISH") |>
    List.map parseIngredient
in 

let parseItem itemString =
    if itemString = "NONE" then 
        Nothing 
    else if String.sub itemString 0 4 = "DISH" then 
        Dish (parseDish itemString)
    else 
        Ingredient (parseIngredient itemString)
in

let parseOrder line  = 
    Scanf.sscanf line "%s %d" (fun dish award -> (parseDish dish, award))  
in 

let explode s =
  let rec exp i l =
    if i < 0 then l else exp (i - 1) (s.[i] :: l) in
  exp (String.length s - 1) []
in

let parseTileLine line y  = List.mapi 
    (fun x c -> ((x,y), match c with 
        | '#' -> Table
        | 'D' -> Dish
        | 'W' -> Bell
        | 'B' -> Source BlueBerry
        | 'I' -> Source IceCream
        | _ -> EmptyTile))
    (explode line)
in

let buildGrid tileList = 
    let grid = Array.make_matrix 11 7 EmptyTile in
    List.iter (fun ((x,y), tile) -> Array.set grid.(x) y tile) tileList;
    grid
in

(* action functions *)

let use ((x,y),_) = Printf.sprintf ("USE %d %d;OCAML STARTER AI") x y |>  print_endline in

let find tileList tile =
    List.find (fun (_, t) -> t = tile) tileList 
in

let findemptytable tileList tablewithitems =
    List.filter (fun (_,tile) -> match tile with Table -> true | _ -> false) tileList |>
    List.find (fun (point,tile) -> not (List.exists (fun (p,_) -> p = point) tablewithitems)) 
in
(* Init *)

let numallcustomers = int_of_string (print (input_line stdin)) in

let orders = List.init numallcustomers (fun _ -> parseOrder (print (input_line stdin)))  in

let tileList  = List.flatten (List.init 7 (fun y -> parseTileLine (print (input_line stdin)) y)) in

let grid = buildGrid tileList in

(* game loop *)

while true do
    let turnsremaining = int_of_string (input_line stdin) in

    let line = print(input_line stdin) in
    let playerx, playery, playeritem = Scanf.sscanf line "%d %d %s" (fun playerx playery playeritem -> (playerx, playery, parseItem playeritem)) in

    let line = input_line stdin in
    let partnerx, partnery, partneritem = Scanf.sscanf line "%d %d %s" (fun partnerx partnery partneritem -> (partnerx, partnery, parseItem partneritem)) in
    
    let numtableswithitems = int_of_string (input_line stdin) in (* the number of tables in the kitchen that currently hold an item *)
    let tablewithitems = List.init numtableswithitems (fun _ -> Scanf.sscanf (input_line stdin) "%d %d %s" (fun tablex tabley item -> ((tablex, tabley), parseItem item))) in
    
    (* ovencontents: ignore until bronze league *)

    let line = input_line stdin in
    let ovencontents, oventimer = Scanf.sscanf line "%s %d" (fun ovencontents oventimer -> (ovencontents, oventimer)) in
    let numcustomers = int_of_string (input_line stdin) in (* the number of customers currently waiting for food *)
    let orders = List.init numcustomers (fun i -> parseOrder (input_line stdin)) in

    (* Write an action using print_endline *)
    (* To debug: prerr_endline "Debug message"; *)
    

    (* MOVE x y *)
    (* USE x y *)
    (* WAIT *)

    match playeritem with
    | Nothing -> find tileList Dish |> use
    | Dish [] -> find tileList (Source BlueBerry) |> use
    | Dish _ -> findemptytable tileList tablewithitems |> use
    | _ -> print_endline "WAIT"
done;