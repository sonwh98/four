(ns four.table)

(def elements [{:element/symbol "H",
                :element/name "Hydrogen",
                :element/weight "1.00794",
                :element/x 1,
                :element/y 1}
               {:element/symbol "He",
                :element/name "Helium",
                :element/weight "4.002602",
                :element/x 18,
                :element/y 1}
               {:element/symbol "Li",
                :element/name "Lithium",
                :element/weight "6.941",
                :element/x 1,
                :element/y 2}
               {:element/symbol "Be",
                :element/name "Beryllium",
                :element/weight "9.012182",
                :element/x 2,
                :element/y 2}
               {:element/symbol "B",
                :element/name "Boron",
                :element/weight "10.811",
                :element/x 13,
                :element/y 2}
               {:element/symbol "C",
                :element/name "Carbon",
                :element/weight "12.0107",
                :element/x 14,
                :element/y 2}
               {:element/symbol "N",
                :element/name "Nitrogen",
                :element/weight "14.0067",
                :element/x 15,
                :element/y 2}
               {:element/symbol "O",
                :element/name "Oxygen",
                :element/weight "15.9994",
                :element/x 16,
                :element/y 2}
               {:element/symbol "F",
                :element/name "Fluorine",
                :element/weight "18.9984032",
                :element/x 17,
                :element/y 2}
               {:element/symbol "Ne",
                :element/name "Neon",
                :element/weight "20.1797",
                :element/x 18,
                :element/y 2}
               {:element/symbol "Na",
                :element/name "Sodium",
                :element/weight "22.98976...",
                :element/x 1,
                :element/y 3}
               {:element/symbol "Mg",
                :element/name "Magnesium",
                :element/weight "24.305",
                :element/x 2,
                :element/y 3}
               {:element/symbol "Al",
                :element/name "Aluminium",
                :element/weight "26.9815386",
                :element/x 13,
                :element/y 3}
               {:element/symbol "Si",
                :element/name "Silicon",
                :element/weight "28.0855",
                :element/x 14,
                :element/y 3}
               {:element/symbol "P",
                :element/name "Phosphorus",
                :element/weight "30.973762",
                :element/x 15,
                :element/y 3}
               {:element/symbol "S",
                :element/name "Sulfur",
                :element/weight "32.065",
                :element/x 16,
                :element/y 3}
               {:element/symbol "Cl",
                :element/name "Chlorine",
                :element/weight "35.453",
                :element/x 17,
                :element/y 3}
               {:element/symbol "Ar",
                :element/name "Argon",
                :element/weight "39.948",
                :element/x 18,
                :element/y 3}
               {:element/symbol "K",
                :element/name "Potassium",
                :element/weight "39.948",
                :element/x 1,
                :element/y 4}
               {:element/symbol "Ca",
                :element/name "Calcium",
                :element/weight "40.078",
                :element/x 2,
                :element/y 4}
               {:element/symbol "Sc",
                :element/name "Scandium",
                :element/weight "44.955912",
                :element/x 3,
                :element/y 4}
               {:element/symbol "Ti",
                :element/name "Titanium",
                :element/weight "47.867",
                :element/x 4,
                :element/y 4}
               {:element/symbol "V",
                :element/name "Vanadium",
                :element/weight "50.9415",
                :element/x 5,
                :element/y 4}
               {:element/symbol "Cr",
                :element/name "Chromium",
                :element/weight "51.9961",
                :element/x 6,
                :element/y 4}
               {:element/symbol "Mn",
                :element/name "Manganese",
                :element/weight "54.938045",
                :element/x 7,
                :element/y 4}
               {:element/symbol "Fe",
                :element/name "Iron",
                :element/weight "55.845",
                :element/x 8,
                :element/y 4}
               {:element/symbol "Co",
                :element/name "Cobalt",
                :element/weight "58.933195",
                :element/x 9,
                :element/y 4}
               {:element/symbol "Ni",
                :element/name "Nickel",
                :element/weight "58.6934",
                :element/x 10,
                :element/y 4}
               {:element/symbol "Cu",
                :element/name "Copper",
                :element/weight "63.546",
                :element/x 11,
                :element/y 4}
               {:element/symbol "Zn",
                :element/name "Zinc",
                :element/weight "65.38",
                :element/x 12,
                :element/y 4}
               {:element/symbol "Ga",
                :element/name "Gallium",
                :element/weight "69.723",
                :element/x 13,
                :element/y 4}
               {:element/symbol "Ge",
                :element/name "Germanium",
                :element/weight "72.63",
                :element/x 14,
                :element/y 4}
               {:element/symbol "As",
                :element/name "Arsenic",
                :element/weight "74.9216",
                :element/x 15,
                :element/y 4}
               {:element/symbol "Se",
                :element/name "Selenium",
                :element/weight "78.96",
                :element/x 16,
                :element/y 4}
               {:element/symbol "Br",
                :element/name "Bromine",
                :element/weight "79.904",
                :element/x 17,
                :element/y 4}
               {:element/symbol "Kr",
                :element/name "Krypton",
                :element/weight "83.798",
                :element/x 18,
                :element/y 4}
               {:element/symbol "Rb",
                :element/name "Rubidium",
                :element/weight "85.4678",
                :element/x 1,
                :element/y 5}
               {:element/symbol "Sr",
                :element/name "Strontium",
                :element/weight "87.62",
                :element/x 2,
                :element/y 5}
               {:element/symbol "Y",
                :element/name "Yttrium",
                :element/weight "88.90585",
                :element/x 3,
                :element/y 5}
               {:element/symbol "Zr",
                :element/name "Zirconium",
                :element/weight "91.224",
                :element/x 4,
                :element/y 5}
               {:element/symbol "Nb",
                :element/name "Niobium",
                :element/weight "92.90628",
                :element/x 5,
                :element/y 5}
               {:element/symbol "Mo",
                :element/name "Molybdenum",
                :element/weight "95.96",
                :element/x 6,
                :element/y 5}
               {:element/symbol "Tc",
                :element/name "Technetium",
                :element/weight "(98)",
                :element/x 7,
                :element/y 5}
               {:element/symbol "Ru",
                :element/name "Ruthenium",
                :element/weight "101.07",
                :element/x 8,
                :element/y 5}
               {:element/symbol "Rh",
                :element/name "Rhodium",
                :element/weight "102.9055",
                :element/x 9,
                :element/y 5}
               {:element/symbol "Pd",
                :element/name "Palladium",
                :element/weight "106.42",
                :element/x 10,
                :element/y 5}
               {:element/symbol "Ag",
                :element/name "Silver",
                :element/weight "107.8682",
                :element/x 11,
                :element/y 5}
               {:element/symbol "Cd",
                :element/name "Cadmium",
                :element/weight "112.411",
                :element/x 12,
                :element/y 5}
               {:element/symbol "In",
                :element/name "Indium",
                :element/weight "114.818",
                :element/x 13,
                :element/y 5}
               {:element/symbol "Sn",
                :element/name "Tin",
                :element/weight "118.71",
                :element/x 14,
                :element/y 5}
               {:element/symbol "Sb",
                :element/name "Antimony",
                :element/weight "121.76",
                :element/x 15,
                :element/y 5}
               {:element/symbol "Te",
                :element/name "Tellurium",
                :element/weight "127.6",
                :element/x 16,
                :element/y 5}
               {:element/symbol "I",
                :element/name "Iodine",
                :element/weight "126.90447",
                :element/x 17,
                :element/y 5}
               {:element/symbol "Xe",
                :element/name "Xenon",
                :element/weight "131.293",
                :element/x 18,
                :element/y 5}
               {:element/symbol "Cs",
                :element/name "Caesium",
                :element/weight "132.9054",
                :element/x 1,
                :element/y 6}
               {:element/symbol "Ba",
                :element/name "Barium",
                :element/weight "132.9054",
                :element/x 2,
                :element/y 6}
               {:element/symbol "La",
                :element/name "Lanthanum",
                :element/weight "138.90547",
                :element/x 4,
                :element/y 9}
               {:element/symbol "Ce",
                :element/name "Cerium",
                :element/weight "140.116",
                :element/x 5,
                :element/y 9}
               {:element/symbol "Pr",
                :element/name "Praseodymium",
                :element/weight "140.90765",
                :element/x 6,
                :element/y 9}
               {:element/symbol "Nd",
                :element/name "Neodymium",
                :element/weight "144.242",
                :element/x 7,
                :element/y 9}
               {:element/symbol "Pm",
                :element/name "Promethium",
                :element/weight "(145)",
                :element/x 8,
                :element/y 9}
               {:element/symbol "Sm",
                :element/name "Samarium",
                :element/weight "150.36",
                :element/x 9,
                :element/y 9}
               {:element/symbol "Eu",
                :element/name "Europium",
                :element/weight "151.964",
                :element/x 10,
                :element/y 9}
               {:element/symbol "Gd",
                :element/name "Gadolinium",
                :element/weight "157.25",
                :element/x 11,
                :element/y 9}
               {:element/symbol "Tb",
                :element/name "Terbium",
                :element/weight "158.92535",
                :element/x 12,
                :element/y 9}
               {:element/symbol "Dy",
                :element/name "Dysprosium",
                :element/weight "162.5",
                :element/x 13,
                :element/y 9}
               {:element/symbol "Ho",
                :element/name "Holmium",
                :element/weight "164.93032",
                :element/x 14,
                :element/y 9}
               {:element/symbol "Er",
                :element/name "Erbium",
                :element/weight "167.259",
                :element/x 15,
                :element/y 9}
               {:element/symbol "Tm",
                :element/name "Thulium",
                :element/weight "168.93421",
                :element/x 16,
                :element/y 9}
               {:element/symbol "Yb",
                :element/name "Ytterbium",
                :element/weight "173.054",
                :element/x 17,
                :element/y 9}
               {:element/symbol "Lu",
                :element/name "Lutetium",
                :element/weight "174.9668",
                :element/x 18,
                :element/y 9}
               {:element/symbol "Hf",
                :element/name "Hafnium",
                :element/weight "178.49",
                :element/x 4,
                :element/y 6}
               {:element/symbol "Ta",
                :element/name "Tantalum",
                :element/weight "180.94788",
                :element/x 5,
                :element/y 6}
               {:element/symbol "W",
                :element/name "Tungsten",
                :element/weight "183.84",
                :element/x 6,
                :element/y 6}
               {:element/symbol "Re",
                :element/name "Rhenium",
                :element/weight "186.207",
                :element/x 7,
                :element/y 6}
               {:element/symbol "Os",
                :element/name "Osmium",
                :element/weight "190.23",
                :element/x 8,
                :element/y 6}
               {:element/symbol "Ir",
                :element/name "Iridium",
                :element/weight "192.217",
                :element/x 9,
                :element/y 6}
               {:element/symbol "Pt",
                :element/name "Platinum",
                :element/weight "195.084",
                :element/x 10,
                :element/y 6}
               {:element/symbol "Au",
                :element/name "Gold",
                :element/weight "196.966569",
                :element/x 11,
                :element/y 6}
               {:element/symbol "Hg",
                :element/name "Mercury",
                :element/weight "200.59",
                :element/x 12,
                :element/y 6}
               {:element/symbol "Tl",
                :element/name "Thallium",
                :element/weight "204.3833",
                :element/x 13,
                :element/y 6}
               {:element/symbol "Pb",
                :element/name "Lead",
                :element/weight "207.2",
                :element/x 14,
                :element/y 6}
               {:element/symbol "Bi",
                :element/name "Bismuth",
                :element/weight "208.9804",
                :element/x 15,
                :element/y 6}
               {:element/symbol "Po",
                :element/name "Polonium",
                :element/weight "(209)",
                :element/x 16,
                :element/y 6}
               {:element/symbol "At",
                :element/name "Astatine",
                :element/weight "(210)",
                :element/x 17,
                :element/y 6}
               {:element/symbol "Rn",
                :element/name "Radon",
                :element/weight "(222)",
                :element/x 18,
                :element/y 6}
               {:element/symbol "Fr",
                :element/name "Francium",
                :element/weight "(223)",
                :element/x 1,
                :element/y 7}
               {:element/symbol "Ra",
                :element/name "Radium",
                :element/weight "(226)",
                :element/x 2,
                :element/y 7}
               {:element/symbol "Ac",
                :element/name "Actinium",
                :element/weight "(227)",
                :element/x 4,
                :element/y 10}
               {:element/symbol "Th",
                :element/name "Thorium",
                :element/weight "232.03806",
                :element/x 5,
                :element/y 10}
               {:element/symbol "Pa",
                :element/name "Protactinium",
                :element/weight "231.0588",
                :element/x 6,
                :element/y 10}
               {:element/symbol "U",
                :element/name "Uranium",
                :element/weight "238.02891",
                :element/x 7,
                :element/y 10}
               {:element/symbol "Np",
                :element/name "Neptunium",
                :element/weight "(237)",
                :element/x 8,
                :element/y 10}
               {:element/symbol "Pu",
                :element/name "Plutonium",
                :element/weight "(244)",
                :element/x 9,
                :element/y 10}
               {:element/symbol "Am",
                :element/name "Americium",
                :element/weight "(243)",
                :element/x 10,
                :element/y 10}
               {:element/symbol "Cm",
                :element/name "Curium",
                :element/weight "(247)",
                :element/x 11,
                :element/y 10}
               {:element/symbol "Bk",
                :element/name "Berkelium",
                :element/weight "(247)",
                :element/x 12,
                :element/y 10}
               {:element/symbol "Cf",
                :element/name "Californium",
                :element/weight "(251)",
                :element/x 13,
                :element/y 10}
               {:element/symbol "Es",
                :element/name "Einstenium",
                :element/weight "(252)",
                :element/x 14,
                :element/y 10}
               {:element/symbol "Fm",
                :element/name "Fermium",
                :element/weight "(257)",
                :element/x 15,
                :element/y 10}
               {:element/symbol "Md",
                :element/name "Mendelevium",
                :element/weight "(258)",
                :element/x 16,
                :element/y 10}
               {:element/symbol "No",
                :element/name "Nobelium",
                :element/weight "(259)",
                :element/x 17,
                :element/y 10}
               {:element/symbol "Lr",
                :element/name "Lawrencium",
                :element/weight "(262)",
                :element/x 18,
                :element/y 10}
               {:element/symbol "Rf",
                :element/name "Rutherfordium",
                :element/weight "(267)",
                :element/x 4,
                :element/y 7}
               {:element/symbol "Db",
                :element/name "Dubnium",
                :element/weight "(268)",
                :element/x 5,
                :element/y 7}
               {:element/symbol "Sg",
                :element/name "Seaborgium",
                :element/weight "(271)",
                :element/x 6,
                :element/y 7}
               {:element/symbol "Bh",
                :element/name "Bohrium",
                :element/weight "(272)",
                :element/x 7,
                :element/y 7}
               {:element/symbol "Hs",
                :element/name "Hassium",
                :element/weight "(270)",
                :element/x 8,
                :element/y 7}
               {:element/symbol "Mt",
                :element/name "Meitnerium",
                :element/weight "(276)",
                :element/x 9,
                :element/y 7}
               {:element/symbol "Ds",
                :element/name "Darmstadium",
                :element/weight "(281)",
                :element/x 10,
                :element/y 7}
               {:element/symbol "Rg",
                :element/name "Roentgenium",
                :element/weight "(280)",
                :element/x 11,
                :element/y 7}
               {:element/symbol "Cn",
                :element/name "Copernicium",
                :element/weight "(285)",
                :element/x 12,
                :element/y 7}
               {:element/symbol "Uut",
                :element/name "Unutrium",
                :element/weight "(284)",
                :element/x 13,
                :element/y 7}
               {:element/symbol "Fl",
                :element/name "Flerovium",
                :element/weight "(289)",
                :element/x 14,
                :element/y 7}
               {:element/symbol "Uup",
                :element/name "Ununpentium",
                :element/weight "(288)",
                :element/x 15,
                :element/y 7}
               {:element/symbol "Lv",
                :element/name "Livermorium",
                :element/weight "(293)",
                :element/x 16,
                :element/y 7}
               {:element/symbol "Uus",
                :element/name "Ununseptium",
                :element/weight "(294)",
                :element/x 17,
                :element/y 7}
               {:element/symbol "Uuo",
                :element/name "Ununoctium",
                :element/weight "(294)",
                :element/x 18,
                :element/y 7}])


(def foo [{:x 1, :y 1}
          {:x 18, :y 1}
          {:x 1, :y 2}
          {:x 2, :y 2}
          {:x 13, :y 2}
          {:x 14, :y 2}
          {:x 15, :y 2}
          {:x 16, :y 2}
          {:x 17, :y 2}
          {:x 18, :y 2}
          {:x 1, :y 3}
          {:x 2, :y 3}
          {:x 13, :y 3}
          {:x 14, :y 3}
          {:x 15, :y 3}
          {:x 16, :y 3}
          {:x 17, :y 3}
          {:x 18, :y 3}
          {:x 1, :y 4}
          {:x 2, :y 4}
          {:x 3, :y 4}
          {:x 4, :y 4}
          {:x 5, :y 4}
          {:x 6, :y 4}
          {:x 7, :y 4}
          {:x 8, :y 4}
          {:x 9, :y 4}
          {:x 10, :y 4}
          {:x 11, :y 4}
          {:x 12, :y 4}
          {:x 13, :y 4}
          {:x 14, :y 4}
          {:x 15, :y 4}
          {:x 16, :y 4}
          {:x 17, :y 4}
          {:x 18, :y 4}
          {:x 1, :y 5}
          {:x 2, :y 5}
          {:x 3, :y 5}
          {:x 4, :y 5}
          {:x 5, :y 5}
          {:x 6, :y 5}
          {:x 7, :y 5}
          {:x 8, :y 5}
          {:x 9, :y 5}
          {:x 10, :y 5}
          {:x 11, :y 5}
          {:x 12, :y 5}
          {:x 13, :y 5}
          {:x 14, :y 5}
          {:x 15, :y 5}
          {:x 16, :y 5}
          {:x 17, :y 5}
          {:x 18, :y 5}
          {:x 1, :y 6}
          {:x 2, :y 6}
          {:x 4, :y 9}
          {:x 5, :y 9}
          {:x 6, :y 9}
          {:x 7, :y 9}
          {:x 8, :y 9}
          {:x 9, :y 9}
          {:x 10, :y 9}
          {:x 11, :y 9}
          {:x 12, :y 9}
          {:x 13, :y 9}
          {:x 14, :y 9}
          {:x 15, :y 9}
          {:x 16, :y 9}
          {:x 17, :y 9}
          {:x 18, :y 9}
          {:x 4, :y 6}
          {:x 5, :y 6}
          {:x 6, :y 6}
          {:x 7, :y 6}
          {:x 8, :y 6}
          {:x 9, :y 6}
          {:x 10, :y 6}
          {:x 11, :y 6}
          {:x 12, :y 6}
          {:x 13, :y 6}
          {:x 14, :y 6}
          {:x 15, :y 6}
          {:x 16, :y 6}
          {:x 17, :y 6}
          {:x 18, :y 6}
          {:x 1, :y 7}
          {:x 2, :y 7}
          {:x 4, :y 10}
          {:x 5, :y 10}
          {:x 6, :y 10}
          {:x 7, :y 10}
          {:x 8, :y 10}
          {:x 9, :y 10}
          {:x 10, :y 10}
          {:x 11, :y 10}
          {:x 12, :y 10}
          {:x 13, :y 10}
          {:x 14, :y 10}
          {:x 15, :y 10}
          {:x 16, :y 10}
          {:x 17, :y 10}
          {:x 18, :y 10}
          {:x 4, :y 7}
          {:x 5, :y 7}
          {:x 6, :y 7}
          {:x 7, :y 7}
          {:x 8, :y 7}
          {:x 9, :y 7}
          {:x 10, :y 7}
          {:x 11, :y 7}
          {:x 12, :y 7}
          {:x 13, :y 7}
          {:x 14, :y 7} 
          {:x 15, :y 7} 
          {:x 16, :y 7} 
          {:x 17, :y 7} 
          {:x 18, :y 7}])
