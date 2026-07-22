"""
Fake ELR (Electronic Laboratory Report) HL7 message generator.

Ported from NEDSS-DataReporting/testing-tools/performance-testing/generate.py
and folded into this project so the ingest CLI can generate synthetic test
data directly, without a separate tool/repo.
"""

import random
import string
from datetime import datetime, timedelta
from pathlib import Path
from typing import List

from faker import Faker

fake = Faker()

FIELD_SEP = "|"
COMPONENT_SEP = "^"
REPEAT_SEP = "~"
ESCAPE_CHAR = "\\"
SUBCOMPONENT_SEP = "&"
SEGMENT_SEP = "\r"

# Sample lab tests for different program areas. Each uses a LOINC code found
# in NBS_SRTE dev data, mapping tests to condition codes, and condition codes
# to program areas (see e.g. Loinc_condition / Snomed_condition).
LAB_TESTS = [
    {"loinc": "30178-8", "name": "West Nile virus Ab", "specimen": "SER", "specimen_txt": "Serum", "results": ["Detected", "Not Detected"]},
    {"loinc": "6388-3", "name": "Eastern equine encephalitis virus Ab", "specimen": "SER", "specimen_txt": "Serum", "results": ["Detected", "Not Detected"]},
    {"loinc": "62493-2", "name": "Streptococcus pneumoniae Ag", "specimen": "BLDV", "specimen_txt": "Blood venous", "results": ["Streptococcus pneumoniae", "Not Detected"]},
    {"loinc": "55161-4", "name": "Bordetella pertussis", "specimen": "NOS", "specimen_txt": "Nose (nasal passage)", "results": ["Detected", "Not Detected"]},
    {"loinc": "65633-0", "name": "Hepatitis B virus", "specimen": "SER", "specimen_txt": "Serum", "results": ["Detected", "Not Detected"]},
    {"loinc": "75886-2", "name": "Hepatitis C virus", "specimen": "SER", "specimen_txt": "Serum", "results": ["Detected", "Not Detected"]},
    {"loinc": "10351-5", "name": "HIV 1 Ab", "specimen": "BLDV", "specimen_txt": "Blood venous", "results": ["Detected", "Not Detected"]},
    {"loinc": "70161-5", "name": "Chlamydia trachomatis", "specimen": "GEN", "specimen_txt": "Genital", "results": ["Detected", "Not Detected"]},
    {"loinc": "64084-7", "name": "Mycobacterium tuberculosis", "specimen": "SPT", "specimen_txt": "Sputum", "results": ["Detected", "Not Detected"]},
    {"loinc": "5401-5", "name": "Varicella zoster virus", "specimen": "BRO", "specimen_txt": "Bronchial", "results": ["Detected", "Not Detected"]},
]

FACILITIES = [
    {"name": "St Mungos Hospital", "clia": "12D3456789", "npi": "1234567890"},
    {"name": "Hogwarts Infirmary", "clia": "34D5678901", "npi": "2345678901"},
    {"name": "Diagon Alley Diagnostics", "clia": "45D6789012", "npi": "3456789012"},
    {"name": "Hogsmeade Health Lab", "clia": "56D7890123", "npi": "4567890123"},
    {"name": "Madam Pomfrey Clinical Services", "clia": "67D8901234", "npi": "5678901234"},
    {"name": "Ministry of Magic Medical Division", "clia": "78D9012345", "npi": "6789012345"},
    {"name": "Slug and Jiggers Apothecary Lab", "clia": "89D0123456", "npi": "7890123456"},
    {"name": "Knockturn Alley Specimen Services", "clia": "90D1234567", "npi": "8901234567"},
    {"name": "Order of the Phoenix Medical", "clia": "01D2345678", "npi": "9012345678"},
    {"name": "Dumbledore Memorial Laboratory", "clia": "23D4567890", "npi": "0123456789"},
]

RACES = [
    ("2106-3", "White"),
    ("2054-5", "Black or African American"),
    ("2028-9", "Asian"),
    ("1002-5", "American Indian or Alaska Native"),
    ("2076-8", "Native Hawaiian or Other Pacific Islander"),
    ("2131-1", "Other Race"),
]

ETHNICITIES = [
    ("2135-2", "Hispanic or Latino"),
    ("2186-5", "Not Hispanic or Latino"),
]

GA_CITIES = [
    "Atlanta", "Savannah", "Augusta", "Macon", "Columbus",
    "Athens", "Sandy Springs", "Roswell", "Albany", "Marietta",
    "Alpharetta", "Johns Creek", "Valdosta", "Smyrna", "Dunwoody",
    "Rome", "Peachtree City", "Gainesville", "Warner Robins", "Decatur",
]

# zipcodes known in dev instances in NBS_SRTE.dbo.Jurisdiction_participation with unique jurisdictions
GA_ZIPCODES = [
    "30309", "30311", "30331", "30342", "31106", "31107", "31126", "31131", "31139", "30029", "78613", "30322"
]

HP_CHARACTERS = [
    {"first": "Harry", "last": "Potter", "gender": "M"},
    {"first": "Hermione", "last": "Granger", "gender": "F"},
    {"first": "Ron", "last": "Weasley", "gender": "M"},
    {"first": "Albus", "last": "Dumbledore", "gender": "M"},
    {"first": "Severus", "last": "Snape", "gender": "M"},
    {"first": "Minerva", "last": "McGonagall", "gender": "F"},
    {"first": "Rubeus", "last": "Hagrid", "gender": "M"},
    {"first": "Draco", "last": "Malfoy", "gender": "M"},
    {"first": "Luna", "last": "Lovegood", "gender": "F"},
    {"first": "Neville", "last": "Longbottom", "gender": "M"},
    {"first": "Ginny", "last": "Weasley", "gender": "F"},
    {"first": "Fred", "last": "Weasley", "gender": "M"},
    {"first": "George", "last": "Weasley", "gender": "M"},
    {"first": "Sirius", "last": "Black", "gender": "M"},
    {"first": "Remus", "last": "Lupin", "gender": "M"},
    {"first": "Bellatrix", "last": "Lestrange", "gender": "F"},
    {"first": "Nymphadora", "last": "Tonks", "gender": "F"},
    {"first": "Cedric", "last": "Diggory", "gender": "M"},
    {"first": "Cho", "last": "Chang", "gender": "F"},
    {"first": "Dobby", "last": "Elf", "gender": "M"},
    {"first": "Lucius", "last": "Malfoy", "gender": "M"},
    {"first": "Narcissa", "last": "Malfoy", "gender": "F"},
    {"first": "Arthur", "last": "Weasley", "gender": "M"},
    {"first": "Molly", "last": "Weasley", "gender": "F"},
    {"first": "Percy", "last": "Weasley", "gender": "M"},
    {"first": "Bill", "last": "Weasley", "gender": "M"},
    {"first": "Charlie", "last": "Weasley", "gender": "M"},
    {"first": "Fleur", "last": "Delacour", "gender": "F"},
    {"first": "Viktor", "last": "Krum", "gender": "M"},
    {"first": "Dolores", "last": "Umbridge", "gender": "F"},
    {"first": "Gilderoy", "last": "Lockhart", "gender": "M"},
    {"first": "Horace", "last": "Slughorn", "gender": "M"},
    {"first": "Argus", "last": "Filch", "gender": "M"},
    {"first": "Pomona", "last": "Sprout", "gender": "F"},
    {"first": "Filius", "last": "Flitwick", "gender": "M"},
    {"first": "Sybill", "last": "Trelawney", "gender": "F"},
    {"first": "Tom", "last": "Riddle", "gender": "M"},
    {"first": "Gellert", "last": "Grindelwald", "gender": "M"},
    {"first": "Newt", "last": "Scamander", "gender": "M"},
    {"first": "Kingsley", "last": "Shacklebolt", "gender": "M"},
]


def generate_control_id() -> str:
    timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
    random_suffix = "".join(random.choices(string.ascii_uppercase + string.digits, k=6))
    return f"{timestamp}{random_suffix}"


def format_hl7_datetime(dt=None, precision="full") -> str:
    if dt is None:
        dt = datetime.now()
    if precision == "date":
        return dt.strftime("%Y%m%d")
    return dt.strftime("%Y%m%d%H%M%S")


def generate_msh_segment(sending_facility, message_type="ORU^R01^ORU_R01") -> str:
    fields = [
        "MSH",
        f"{COMPONENT_SEP}{REPEAT_SEP}{ESCAPE_CHAR}{SUBCOMPONENT_SEP}",
        f"{sending_facility['name']}^{sending_facility['clia']}^CLIA",
        f"{sending_facility['name']}^{sending_facility['npi']}^NPI",
        "State Public Health Lab^2.16.840.1.114222^ISO",
        "State DOH^2.16.840.1.114222.1^ISO",
        format_hl7_datetime(),
        "",
        message_type,
        generate_control_id(),
        "P",
        "2.5.1",
        "",
        "",
        "NE",
        "NE",
        "USA",
        "UNICODE UTF-8",
        "",
        "",
        "PHLabReport-NoAck^ELR251R1_Rcvr_Prof^2.16.840.1.113883.9.11^ISO",
    ]
    return FIELD_SEP.join(fields)


def generate_sft_segment() -> str:
    fields = ["SFT", "Fake ELR Generator^L", "1.0", "FakeELR", "1.0.0", "", ""]
    return FIELD_SEP.join(fields)


def generate_pid_segment() -> str:
    patient_number = "".join(random.choices(string.ascii_letters + string.digits, k=8))
    character = random.choice(HP_CHARACTERS)

    first_name = character["first"]
    last_name = f"{character['last']}_{patient_number}"
    gender = character["gender"]

    dob = fake.date_of_birth(minimum_age=1, maximum_age=90)
    race = random.choice(RACES)
    ethnicity = random.choice(ETHNICITIES)

    patient_id = f"HP{patient_number}"
    ssn = f"{random.randint(100, 999)}-{random.randint(10, 99)}-{random.randint(1000, 9999)}"

    address = fake.street_address()
    city = random.choice(GA_CITIES)
    state = "GA"
    zipcode = random.choice(GA_ZIPCODES)
    phone_digits = "".join(c for c in fake.phone_number() if c.isdigit())
    if len(phone_digits) > 10:
        if phone_digits.startswith("001"):
            phone_digits = phone_digits[3:]
        elif phone_digits[0] == "1":
            phone_digits = phone_digits[1:]
    phone = phone_digits[:10]

    fields = [
        "PID",
        "1",
        "",
        f"{patient_id}^^^{random.choice(FACILITIES)['name']}&2.16.840.1.113883.19.4.6&ISO^MR",
        "",
        f"{last_name}^{first_name}^^^^L",
        "",
        format_hl7_datetime(datetime.combine(dob, datetime.min.time()), "date"),
        gender,
        "",
        f"{race[0]}^{race[1]}^HL70005",
        f"{address}^^{city}^{state}^{zipcode}^USA^H",
        "",
        f"^PRN^PH^^1^{phone[:3]}^{phone[3:]}",
        "",
        "",
        "",
        "",
        "",
        f"{ssn}^^^USA^SS",
        "",
        "",
        f"{ethnicity[0]}^{ethnicity[1]}^HL70189",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
    ]
    return FIELD_SEP.join(fields)


def generate_orc_segment(order_number, facility) -> str:
    provider_npi = "".join(random.choices(string.digits, k=10))
    provider_first = fake.first_name()
    provider_last = fake.last_name() + "_FAKE"

    order_datetime = datetime.now() - timedelta(hours=random.randint(1, 48))

    fields = [
        "ORC",
        "RE",
        f"{order_number}^{facility['name']}^{facility['clia']}^CLIA",
        f"{order_number}^{facility['name']}^{facility['clia']}^CLIA",
        "",
        "CM",
        "",
        "",
        "",
        format_hl7_datetime(order_datetime),
        "",
        "",
        f"{provider_npi}^{provider_last}^{provider_first}^^^^^^NPI^L^^^NPI",
        "",
        "",
        "",
        "",
        f"{facility['name']}^L^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^{facility['clia']}",
        "",
        "",
        "",
        f"{facility['name']}^L^^^^CLIA&2.16.840.1.113883.4.7&ISO^XX^^^{facility['clia']}",
        f"{fake.street_address()}^^{fake.city()}^{fake.state_abbr()}^{fake.zipcode()}^USA^B",
        f"^WPN^PH^^1^{random.randint(200, 999)}^{random.randint(1000000, 9999999)}",
        f"{fake.street_address()}^^{fake.city()}^{fake.state_abbr()}^{fake.zipcode()}^USA^B",
    ]
    return FIELD_SEP.join(fields)


def generate_obr_segment(order_number, test_info, facility) -> str:
    collection_datetime = datetime.now() - timedelta(hours=random.randint(24, 72))
    result_datetime = datetime.now() - timedelta(hours=random.randint(1, 24))

    provider_npi = "".join(random.choices(string.digits, k=10))
    provider_first = fake.first_name()
    provider_last = fake.last_name() + "_FAKE"

    fields = [
        "OBR",
        "1",
        f"{order_number}^{facility['name']}^{facility['clia']}^CLIA",
        f"{order_number}^{facility['name']}^{facility['clia']}^CLIA",
        f"{test_info['loinc']}^{test_info['name']}^LN",
        "",
        "",
        format_hl7_datetime(collection_datetime),
        "",
        "",
        "",
        "",
        "",
        "",
        format_hl7_datetime(collection_datetime),
        f"{test_info['specimen']}^{test_info['specimen']}^HL70487",
        f"{provider_npi}^{provider_last}^{provider_first}^^^^^^NPI^L^^^NPI",
        "",
        "",
        "",
        "",
        "",
        format_hl7_datetime(result_datetime),
        "",
        "",
        "F",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
    ]
    return FIELD_SEP.join(fields)


def generate_obx_segment(set_id, test_info) -> str:
    result = random.choice(test_info["results"])
    result_datetime = datetime.now() - timedelta(hours=random.randint(1, 24))

    is_numeric = result.replace(".", "").isdigit()

    result_codes = {
        "Detected": "260373001",
        "Positive": "10828004",
        "Reactive": "11214006",
        "Not Detected": "260415000",
        "Negative": "260385009",
        "Non-Reactive": "131194007",
        "No Growth": "264868006",
        "Streptococcus pneumoniae": "9861002",
        "Bordetella pertussis": "5247005",
        "Hepatitis B virus": "81665004",
        "Hepatitis C virus": "62944002",
        "Chlamydia trachomatis": "63938009",
        "Mycobacterium tuberculosis": "113861009",
        "Varicella zoster virus": "19551004",
        "HIV 1": "19030005",
    }

    if is_numeric:
        value_type = "NM"
        result_field = result
        units = "ug/dL^ug/dL^UCUM" if "Lead" in test_info["name"] else ""
    else:
        value_type = "CWE"
        result_code = result_codes.get(result, "")
        if result_code:
            result_field = f"{result_code}^{result}^SCT"
        else:
            fallback_code = str(abs(hash(result)) % 900000000 + 100000000)
            result_field = f"{fallback_code}^{result}^L"
        units = ""

    if result in ["Detected", "Positive", "Reactive"] or (is_numeric and float(result) > 5):
        abnormal_flag = "A"
    else:
        abnormal_flag = "N"

    fields = [
        "OBX",
        str(set_id),
        value_type,
        f"{test_info['loinc']}^{test_info['name']}^LN",
        "",
        result_field,
        units,
        "",
        abnormal_flag,
        "",
        "",
        "F",
        "",
        "",
        format_hl7_datetime(result_datetime),
        "",
        "",
        "",
        "",
        format_hl7_datetime(result_datetime),
    ]
    return FIELD_SEP.join(fields)


def generate_spm_segment(test_info) -> str:
    collection_datetime = datetime.now() - timedelta(hours=random.randint(24, 72))
    specimen_id = "".join(random.choices(string.ascii_uppercase + string.digits, k=12))

    fields = [
        "SPM",
        "1",
        f"{specimen_id}^{specimen_id}",
        "",
        f"{test_info['specimen']}^{test_info['specimen_txt']}^HL70487",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        format_hl7_datetime(collection_datetime),
        format_hl7_datetime(collection_datetime + timedelta(hours=2)),
    ]
    return FIELD_SEP.join(fields)


def generate_elr_message() -> str:
    """Generate one complete, correctly CRLF/CR-terminated fake ELR HL7 message."""
    facility = random.choice(FACILITIES)
    test = random.choice(LAB_TESTS)
    order_number = "".join(random.choices(string.digits, k=10))

    segments = [
        generate_msh_segment(facility),
        generate_sft_segment(),
        generate_pid_segment(),
        generate_orc_segment(order_number, facility),
        generate_obr_segment(order_number, test, facility),
        generate_obx_segment(1, test),
        generate_spm_segment(test),
    ]

    return SEGMENT_SEP.join(segments) + SEGMENT_SEP


def generate_files(output_dir: Path, count: int) -> List[Path]:
    """Generate `count` fake ELR HL7 files into output_dir, named 001.hl7, 002.hl7, ..."""
    output_dir.mkdir(parents=True, exist_ok=True)
    digits = max(2, len(str(count)))
    written = []
    for i in range(count):
        message = generate_elr_message()
        filename = f"{str(i + 1).zfill(digits)}.hl7"
        filepath = output_dir / filename
        filepath.write_text(message, newline="")
        written.append(filepath)
    return written
