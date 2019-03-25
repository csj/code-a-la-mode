STDOUT.sync = true
# Ported from kotlin starter because originality is dead -> https://github.com/csj/code-a-la-mode/blob/master/src/test/starterkit/starterAI.kt
# by https://www.codingame.com/profile/1d1729a2d8c008c6cf728ee88f1faa6d4978712 https://github.com/Unihedro follow me pls

def parseIntIntString((a, b, c)) [a.to_i, b.to_i, c] end
def parseStringInt((a, b)) [a, b.to_i] end
    
# <!-- bronze
# [{customer_item: string "the food ordered by the customer",
#   customer_award: integer "number of points for fulfilling the delivery"}]
@customers = gets.to_i.times.map do
    customer_item, customer_award = parseStringInt gets.split
    {customer_item: customer_item, customer_award: customer_award}
end # -->
# {[x, y] "coordinates" => true} for all coordinates which are tables
@tables = {}
# array of strings, makes up the 7x11 map
@map = 7.times.map { |y|
    line = gets.chomp
    line.chars.map.with_index { |v, x|
        @tables[[x, y]] = true if v == '#'
    }
    line
}
# char -> coordinates, else nil if not found
findEquipment = -> searchChar {
    index = nil
    found = @map.index { |x| index = x.index(searchChar) }
    [index, found] if found
}
# so apparently the above function is only used once why did I even optimize it
@equipment_locations = %w|B I D|.map &findEquipment
blueberryXY, iceCreamXY, dishWasherXY = @equipment_locations
@next_equipment_index = 0
@use_empty_table = false
# game loop
loop do
    turns_remaining = gets.to_i
    player_x, player_y, player_item = parseIntIntString gets.split
    partner_x, partner_y, partner_item = parseIntIntString gets.split
    # {[x, y] "coordinates" => string "item"} for all coordinates which are tables with items
    @tables_with_items = gets.to_i.times.map {
        x, y, item = parseIntIntString gets.split
        [[x, y], item]
    }.to_h
    # <!-- wood 1
    oven_contents, oven_timer = parseStringInt gets.split
    @customers = gets.to_i.times.map do
        customer_item, customer_award = parseStringInt gets.split
        {customer_item: customer_item, customer_award: customer_award}
    end # -->

    # Strategy:
    # 1. Grab a blueberry, put it down
    # 2. Grab some ice cream, put it down
    # 3. Grab a plate, put it down
    # 4. Repeat
    target_x, target_y = @use_empty_table ?
        (@tables.keys - @tables_with_items.keys).first # The first empty table.
    :
        @equipment_locations[@next_equipment_index] # The next needed equipment.

    # If we're adjacent to it, it should work.
    dx = player_x - target_x
    dy = player_y - target_y
    if dx.abs <= 1 && dy.abs <= 1
        if @use_empty_table
            @next_equipment_index += 1
            @next_equipment_index = 0 if @next_equipment_index == @equipment_locations.size
        end
        @use_empty_table = !@use_empty_table
    end
    puts "USE #{target_x} #{target_y}; Ruby Starter AI"
end
