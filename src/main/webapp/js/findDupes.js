function findDupes(text, maxdupes) {
    var tokens = text.toLowerCase().split(/[\W\s]+/);
    console.log(tokens);
    var tpos = {};
    var dupcount = [];
    for (var i = 0; i < tokens.length && dupcount.length < maxdupes; i++) {
        if (!tpos[tokens[i]]) {
            tpos[tokens[i]] = [i];
            dupcount = [];
        } else {
            tpos[tokens[i]].push(i);
            if (dupcount.length > 0) {
                var found = false;
                for (var j = 0; j < tpos[tokens[i]].length - 1; j++) {
                    console.log("Compare "+tokens[tpos[tokens[j]] - 1]+" to "+tokens[i - 1]);
                    if (tokens[tpos[tokens[i]][j] - 1] == tokens[i - 1]) {
                        dupcount.push(tokens[i]);
                        console.log(JSON.stringify(dupcount));
                        found = true;
                        break;
                    }
                }
                if (!found) dupcount = [tokens[i]];
            } else {
                dupcount = [tokens[i]];
            }


        }

    }
    console.log(JSON.stringify(tpos));
    console.log("Test");
    console.log(JSON.stringify(dupcount));
}

var text = "Medicaid and the Children?s Health Insurance Program (CHIP) provide health coverage to more than 31 million children, including half of all low-income children in the U.S. The federal government sets minimum guidelines for Medicaid eligibility but states can choose to expand coverage beyond the minimum threshold. In addition, all States have expanded coverage for children through the Children?s Health Insurance Program. CMS has extensive efforts underway, supported by the Children?s Health Insurance Program Reauthorization Act   to work with States and other stakeholders to find and enroll uninsured children who are eligible for Medicaid or CHIP, through the Connecting Kids to Coverage Challenge. Children enrolled in public schools can get pamphlets to bring home to parents outlining simple instructions for parents to determine eligibility and showing a detailed plan for enrolling their children. Reach out and help families enroll their children where they live, learn, play, work, worship, and go for health care or for help with other family needs. Strive to make enrollment assistance an ongoing and routine activity. Children enrolled in public schools can get pamphlets to bring home to parents outlining simple instructions for parents to determine eligibility and showing a detailed plan for enrolling their children. Reach out and help families enroll their children where they live, learn, play, work, worship, and go for health care or for help with other family needs. Strive to make enrollment assistance an ongoing and routine activity.  Help parents to become involves with their child's education and the wellbeing of the child.  Introduce families to government assistants and non profit agencies that would help them obtaining family needs. Medicaid and the Children?s Health Insurance Program provide health coverage to more than 31 million children, including half of all low-income children. Extensive outreach is being done to enroll eligible children in coverage in the Connecting Kids to Coverage Challenge. The federal government sets minimum guidelines for Medicaid eligibility but states can choose to expand coverage beyond the minimum threshold. For more information is available in the CHIP section or on the Children?s page."

findDupes(text, 10);