/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *  
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *
 *     Russell Boykin       - initial API and implementation
 *     Alberto Giammaria    - initial API and implementation
 *     Chris Peters         - initial API and implementation
 *     Gianluca Bernardini  - initial API and implementation
 *******************************************************************************/
package org.eclipse.lyo.oslc4j.bugzilla.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.lyo.oslc4j.bugzilla.Constants;
import org.eclipse.lyo.oslc4j.core.annotation.OslcAllowedValue;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDescription;
import org.eclipse.lyo.oslc4j.core.annotation.OslcName;
import org.eclipse.lyo.oslc4j.core.annotation.OslcOccurs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcRange;
import org.eclipse.lyo.oslc4j.core.annotation.OslcReadOnly;
import org.eclipse.lyo.oslc4j.core.annotation.OslcRepresentation;
import org.eclipse.lyo.oslc4j.core.annotation.OslcTitle;
import org.eclipse.lyo.oslc4j.core.annotation.OslcValueType;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;
import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.model.Occurs;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.Representation;
import org.eclipse.lyo.oslc4j.core.model.ValueType;

public class ChangeRequest
       extends AbstractResource
{
    private final Set<Link>     affectedByDefects           = new HashSet<Link>();
    private final Set<Link>     affectsPlanItems            = new HashSet<Link>();
    private final Set<Link>     affectsRequirements         = new HashSet<Link>();
    private final Set<Link>     affectsTestResults          = new HashSet<Link>();
    private final Set<Link>     blocksTestExecutionRecords  = new HashSet<Link>();
    private final List<Person>   contributors                = new ArrayList<Person>();
    private final List<Person>   creators                    = new ArrayList<Person>();
    private final Set<Type>     dctermsTypes                = new TreeSet<Type>();
    private final Set<Link>     implementsRequirements      = new HashSet<Link>();
    private final Set<Link>     relatedChangeRequests       = new HashSet<Link>();
    private final Set<Link>     relatedResources            = new HashSet<Link>(); // TODO - Extension to point to any other OSLC resource(s).
    private final Set<Link>     relatedTestCases            = new HashSet<Link>();
    private final Set<Link>     relatedTestExecutionRecords = new HashSet<Link>();
    private final Set<Link>     relatedTestPlans            = new HashSet<Link>();
    private final Set<Link>     relatedTestScripts          = new HashSet<Link>();
    private final Set<String>   subjects                    = new TreeSet<String>();
    private final Set<Link>     testedByTestCases           = new HashSet<Link>();
    private final Set<Link>     tracksChangeSets            = new HashSet<Link>();
    private final Set<Link>     tracksRequirements          = new HashSet<Link>();
    private final Set<URI>      rdfTypes                    = new TreeSet<URI>();

    private Boolean  approved;
    private Boolean  closed;
    private Date     closeDate;
    private Date     created;
    private String   description;
    private URI      discussedBy;
    private Boolean  fixed;
    private String   identifier;
    private Boolean  inProgress;
    private URI      instanceShape;
    private Date     modified;
    private Boolean  reviewed;
    private URI      serviceProvider;
    private Severity severity = Severity.Unclassified; // TODO - Added severity for demo
    private String   shortTitle;
    private String   status;
    private String   title;
    private Boolean  verified;

    public ChangeRequest()
           throws URISyntaxException
    {
        super();

        rdfTypes.add(new URI(Constants.TYPE_CHANGE_REQUEST));
    }

    public ChangeRequest(final URI about)
           throws URISyntaxException
    {
        super(about);

        rdfTypes.add(new URI(Constants.TYPE_CHANGE_REQUEST));
    }

    public void addAffectedByDefect(final Link affectedByDefect)
    {
        this.affectedByDefects.add(affectedByDefect);
    }

    public void addAffectsPlanItem(final Link affectsPlanItem)
    {
        this.affectsPlanItems.add(affectsPlanItem);
    }

    public void addAffectsRequirement(final Link affectsRequirement)
    {
        this.affectsRequirements.add(affectsRequirement);
    }

    public void addAffectsTestResult(final Link affectsTestResult)
    {
        this.affectsTestResults.add(affectsTestResult);
    }

    public void addBlocksTestExecutionRecord(final Link blocksTestExecutionRecord)
    {
        this.blocksTestExecutionRecords.add(blocksTestExecutionRecord);
    }

    public void addContributor(final Person contributor)
    {
        this.contributors.add(contributor);
    }

    public void addCreator(final Person creator)
    {
        this.creators.add(creator);
    }

    public void addDctermsType(final String dctermsType)
    {
        this.dctermsTypes.add(Type.fromString(dctermsType));
    }

    public void addImplementsRequirement(final Link implementsRequirement)
    {
        this.implementsRequirements.add(implementsRequirement);
    }

    public void addRdfType(final URI rdfType)
    {
        this.rdfTypes.add(rdfType);
    }

    public void addRelatedChangeRequest(final Link relatedChangeRequest)
    {
        this.relatedChangeRequests.add(relatedChangeRequest);
    }

    public void addRelatedResource(final Link relatedResource)
    {
        this.relatedResources.add(relatedResource);
    }

    public void addRelatedTestCase(final Link relatedTestCase)
    {
        this.relatedTestCases.add(relatedTestCase);
    }

    public void addRelatedTestExecutionRecord(final Link relatedTestExecutionRecord)
    {
        this.relatedTestExecutionRecords.add(relatedTestExecutionRecord);
    }

    public void addRelatedTestPlan(final Link relatedTestPlan)
    {
        this.relatedTestPlans.add(relatedTestPlan);
    }

    public void addRelatedTestScript(final Link relatedTestScript)
    {
        this.relatedTestScripts.add(relatedTestScript);
    }

    public void addSubject(final String subject)
    {
        this.subjects.add(subject);
    }

    public void addTestedByTestCase(final Link testedByTestCase)
    {
        this.testedByTestCases.add(testedByTestCase);
    }

    public void addTracksChangeSet(final Link tracksChangeSet)
    {
        this.tracksChangeSets.add(tracksChangeSet);
    }

    public void addTracksRequirement(final Link tracksRequirement)
    {
        this.tracksRequirements.add(tracksRequirement);
    }

    @OslcDescription("Change request is affected by a reported defect.")
    @OslcName("affectedByDefect")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "affectedByDefect")
    @OslcRange(Constants.TYPE_CHANGE_REQUEST)
    @OslcReadOnly(false)
    @OslcTitle("Affected By Defects")
    public Link[] getAffectedByDefects()
    {
        return affectedByDefects.toArray(new Link[affectedByDefects.size()]);
    }

    @OslcDescription("Change request affects a plan item. ")
    @OslcName("affectsPlanItem")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "affectsPlanItem")
    @OslcRange(Constants.TYPE_CHANGE_REQUEST)
    @OslcReadOnly(false)
    @OslcTitle("Affects Plan Items")
    public Link[] getAffectsPlanItems()
    {
        return affectsPlanItems.toArray(new Link[affectsPlanItems.size()]);
    }

    @OslcDescription("Change request affecting a Requirement.")
    @OslcName("affectsRequirement")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "affectsRequirement")
    @OslcRange(Constants.TYPE_REQUIREMENT)
    @OslcReadOnly(false)
    @OslcTitle("Affects Requirements")
    public Link[] getAffectsRequirements()
    {
        return affectsRequirements.toArray(new Link[affectsRequirements.size()]);
    }

    @OslcDescription("Associated QM resource that is affected by this Change Request.")
    @OslcName("affectsTestResult")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "affectsTestResult")
    @OslcRange(Constants.TYPE_TEST_RESULT)
    @OslcReadOnly(false)
    @OslcTitle("Affects Test Results")
    public Link[] getAffectsTestResults()
    {
        return affectsTestResults.toArray(new Link[affectsTestResults.size()]);
    }

    @OslcDescription("Associated QM resource that is blocked by this Change Request.")
    @OslcName("blocksTestExecutionRecord")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "blocksTestExecutionRecord")
    @OslcRange(Constants.TYPE_TEST_EXECUTION_RECORD)
    @OslcReadOnly(false)
    @OslcTitle("Blocks Test Execution Records")
    public Link[] getBlocksTestExecutionRecords()
    {
        return blocksTestExecutionRecords.toArray(new Link[blocksTestExecutionRecords.size()]);
    }

    @OslcDescription("The date at which no further activity or work is intended to be conducted. ")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "closeDate")
    @OslcReadOnly
    @OslcTitle("Close Date")
    public Date getCloseDate()
    {
        return closeDate;
    }

    @OslcDescription("The person(s) who are responsible for the work needed to complete the change request.")
    @OslcName("contributor")
    @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "contributor")
    @OslcRepresentation(Representation.Inline)
    @OslcValueType(ValueType.LocalResource)
    @OslcRange(Constants.TYPE_PERSON)
    @OslcTitle("Contributors")
    public List<Person> getContributors()
    {
        return contributors;
    }

    @OslcDescription("Timestamp of resource creation.")
    @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "created")
    @OslcReadOnly
    @OslcTitle("Created")
    public Date getCreated()
    {
        return created;
    }

    @OslcDescription("Creator or creators of resource.")
    @OslcName("creator")
    @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "creator")
    @OslcRepresentation(Representation.Inline)
    @OslcValueType(ValueType.LocalResource)
    @OslcRange(Constants.TYPE_PERSON)
    @OslcTitle("Creators")
    public List<Person> getCreators()
    {
        return creators;
    }

    @OslcAllowedValue({"Defect", "Task", "Story", "Bug Report", "Feature Request"})
    @OslcDescription("A short string representation for the type, example 'Defect'.")
    @OslcName("type")
    @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "type")
    @OslcTitle("Types")
    public String[] getDctermsTypes()
    {
        final String[] result = new String[dctermsTypes.size()];

        int index = 0;

        for (final Type type : dctermsTypes)
        {
            result[index++] = type.toString();
        }

        return result;
    }

    @OslcDescription("Descriptive text (reference: Dublin Core) about resource represented as rich text in XHTML content.")
    @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "description")
    @OslcTitle("Description")
    @OslcValueType(ValueType.XMLLiteral)
    public String getDescription()
    {
        return description;
    }

    @OslcDescription("A series of notes and comments about this change request.")
    @OslcPropertyDefinition(OslcConstants.OSLC_CORE_NAMESPACE + "discussedBy")
    @OslcRange(Constants.TYPE_DISCUSSION)
    @OslcTitle("Discussed By")
    public URI getDiscussedBy()
    {
        return discussedBy;
    }

    @OslcDescription("A unique identifier for a resource. Assigned by the service provider when a resource is created. Not intended for end-user display.")
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "identifier")
    @OslcReadOnly
    @OslcTitle("Identifier")
    public String getIdentifier()
    {
        return identifier;
    }

    @OslcDescription("Implements associated Requirement.")
    @OslcName("implementsRequirement")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "implementsRequirement")
    @OslcRange(Constants.TYPE_REQUIREMENT)
    @OslcReadOnly(false)
    @OslcTitle("Implements Requirements")
    public Link[] getImplementsRequirements()
    {
        return implementsRequirements.toArray(new Link[implementsRequirements.size()]);
    }

    @OslcDescription("Resource Shape that provides hints as to resource property value-types and allowed values. ")
    @OslcPropertyDefinition(OslcConstants.OSLC_CORE_NAMESPACE + "instanceShape")
    @OslcRange(OslcConstants.TYPE_RESOURCE_SHAPE)
    @OslcTitle("Instance Shape")
    public URI getInstanceShape()
    {
        return instanceShape;
    }

    @OslcDescription("Timestamp last latest resource modification.")
    @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "modified")
    @OslcReadOnly
    @OslcTitle("Modified")
    public Date getModified()
    {
        return modified;
    }

    @OslcDescription("The resource type URIs.")
    @OslcName("type")
    @OslcPropertyDefinition(OslcConstants.RDF_NAMESPACE + "type")
    @OslcTitle("Types")
    public URI[] getRdfTypes()
    {
        return rdfTypes.toArray(new URI[rdfTypes.size()]);
    }

    @OslcDescription("This relationship is loosely coupled and has no specific meaning.")
    @OslcName("relatedChangeRequest")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "relatedChangeRequest")
    @OslcRange(Constants.TYPE_CHANGE_REQUEST)
    @OslcReadOnly(false)
    @OslcTitle("Related Change Requests")
    public Link[] getRelatedChangeRequests()
    {
        return relatedChangeRequests.toArray(new Link[relatedChangeRequests.size()]);
    }

    @OslcDescription("Related OSLC resources of any type.")
    @OslcName("relatedResource")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "relatedResource")
    @OslcTitle("Related Resources")
    public Link[] getRelatedResources()
    {
        return relatedResources.toArray(new Link[relatedResources.size()]);
    }

    @OslcDescription("Related QM test case resource.")
    @OslcName("relatedTestCase")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "relatedTestCase")
    @OslcRange(Constants.TYPE_TEST_CASE)
    @OslcReadOnly(false)
    @OslcTitle("Related Test Cases")
    public Link[] getRelatedTestCases()
    {
        return relatedTestCases.toArray(new Link[relatedTestCases.size()]);
    }

    @OslcDescription("Related to a QM test execution resource.")
    @OslcName("relatedTestExecutionRecord")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "relatedTestExecutionRecord")
    @OslcRange(Constants.TYPE_TEST_EXECUTION_RECORD)
    @OslcReadOnly(false)
    @OslcTitle("Related Test Execution Records")
    public Link[] getRelatedTestExecutionRecords()
    {
        return relatedTestExecutionRecords.toArray(new Link[relatedTestExecutionRecords.size()]);
    }

    @OslcDescription("Related QM test plan resource.")
    @OslcName("relatedTestPlan")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "relatedTestPlan")
    @OslcRange(Constants.TYPE_TEST_PLAN)
    @OslcReadOnly(false)
    @OslcTitle("Related Test Plans")
    public Link[] getRelatedTestPlans()
    {
        return relatedTestPlans.toArray(new Link[relatedTestPlans.size()]);
    }

    @OslcDescription("Related QM test script resource.")
    @OslcName("relatedTestScript")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "relatedTestScript")
    @OslcRange(Constants.TYPE_TEST_SCRIPT)
    @OslcReadOnly(false)
    @OslcTitle("Related Test Scripts")
    public Link[] getRelatedTestScripts()
    {
        return relatedTestScripts.toArray(new Link[relatedTestScripts.size()]);
    }

    @OslcDescription("The scope of a resource is a URI for the resource's OSLC Service Provider.")
    @OslcPropertyDefinition(OslcConstants.OSLC_CORE_NAMESPACE + "serviceProvider")
    @OslcRange(OslcConstants.TYPE_SERVICE_PROVIDER)
    @OslcTitle("Service Provider")
    public URI getServiceProvider()
    {
        return serviceProvider;
    }

    @OslcAllowedValue({"Unclassified", "Minor", "Normal", "Major", "Critical", "Blocker"})
    @OslcDescription("Severity of change request.")
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "severity")
    @OslcTitle("Severity")
    public String getSeverity()
    {
        return severity.toString();
    }

    @OslcDescription("Short name identifying a resource, often used as an abbreviated identifier for presentation to end-users.")
    @OslcPropertyDefinition(OslcConstants.OSLC_CORE_NAMESPACE + "shortTitle")
    @OslcTitle("Short Title")
    @OslcValueType(ValueType.XMLLiteral)
    public String getShortTitle()
    {
        return shortTitle;
    }

    @OslcDescription("Used to indicate the status of the change request based on values defined by the service provider. Most often a read-only property. Some possible values may include: 'Submitted', 'Done', 'InProgress', etc.")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "status")
    @OslcTitle("Status")
    public String getStatus()
    {
        return status;
    }

    @OslcDescription("Tag or keyword for a resource. Each occurrence of a dcterms:subject property denotes an additional tag for the resource.")
    @OslcName("subject")
    @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "subject")
    @OslcReadOnly(false)
    @OslcTitle("Subjects")
    public String[] getSubjects()
    {
        return subjects.toArray(new String[subjects.size()]);
    }

    @OslcDescription("Test case by which this change request is tested.")
    @OslcName("testedByTestCase")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "testedByTestCase")
    @OslcRange(Constants.TYPE_TEST_CASE)
    @OslcReadOnly(false)
    @OslcTitle("Tested by Test Cases")
    public Link[] getTestedByTestCases()
    {
        return testedByTestCases.toArray(new Link[testedByTestCases.size()]);
    }

    @OslcDescription("Title (reference: Dublin Core) or often a single line summary of the resource represented as rich text in XHTML content.")
    @OslcOccurs(Occurs.ExactlyOne)
    @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "title")
    @OslcTitle("Title")
    @OslcValueType(ValueType.XMLLiteral)
    public String getTitle()
    {
        return title;
    }

    @OslcDescription("Tracks SCM change set resource.")
    @OslcName("tracksChangeSet")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "tracksChangeSet")
    @OslcRange(Constants.TYPE_CHANGE_SET)
    @OslcReadOnly(false)
    @OslcTitle("Tracks Change Sets")
    public Link[] getTracksChangeSets()
    {
        return tracksChangeSets.toArray(new Link[tracksChangeSets.size()]);
    }

    @OslcDescription("Tracks the associated Requirement or Requirement ChangeSet resources.")
    @OslcName("tracksRequirement")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "tracksRequirement")
    @OslcRange(Constants.TYPE_REQUIREMENT)
    @OslcReadOnly(false)
    @OslcTitle("Tracks Requirements")
    public Link[] getTracksRequirements()
    {
        return tracksRequirements.toArray(new Link[tracksRequirements.size()]);
    }

    @OslcDescription("Whether or not the Change Request has been approved.")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "approved")
    @OslcReadOnly
    @OslcTitle("Approved")
    public Boolean isApproved()
    {
        return approved;
    }

    @OslcDescription("Whether or not the Change Request is completely done, no further fixes or fix verification is needed.")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "closed")
    @OslcReadOnly
    @OslcTitle("Closed")
    public Boolean isClosed()
    {
        return closed;
    }

    @OslcDescription("Whether or not the Change Request has been fixed.")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "fixed")
    @OslcReadOnly
    @OslcTitle("Fixed")
    public Boolean isFixed()
    {
        return fixed;
    }

    @OslcDescription("Whether or not the Change Request in a state indicating that active work is occurring. If oslc_cm:inprogress is true, then oslc_cm:fixed and oslc_cm:closed must also be false.")
    @OslcName("inprogress")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "inprogress")
    @OslcReadOnly
    @OslcTitle("In Progress")
    public Boolean isInProgress()
    {
        return inProgress;
    }

    @OslcDescription("Whether or not the Change Request has been reviewed.")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "reviewed")
    @OslcReadOnly
    @OslcTitle("Reviewed")
    public Boolean isReviewed()
    {
        return reviewed;
    }

    @OslcDescription("Whether or not the resolution or fix of the Change Request has been verified.")
    @OslcPropertyDefinition(Constants.CHANGE_MANAGEMENT_NAMESPACE + "verified")
    @OslcReadOnly
    @OslcTitle("Verified")
    public Boolean isVerified()
    {
        return verified;
    }

    public void setAffectedByDefects(final Link[] affectedByDefects)
    {
        this.affectedByDefects.clear();

        if (affectedByDefects != null)
        {
            this.affectedByDefects.addAll(Arrays.asList(affectedByDefects));
        }
    }

    public void setAffectsPlanItems(final Link[] affectsPlanItems)
    {
        this.affectsPlanItems.clear();

        if (affectsPlanItems != null)
        {
            this.affectsPlanItems.addAll(Arrays.asList(affectsPlanItems));
        }
    }

    public void setAffectsRequirements(final Link[] affectsRequirements)
    {
        this.affectsRequirements.clear();

        if (affectsRequirements != null)
        {
            this.affectsRequirements.addAll(Arrays.asList(affectsRequirements));
        }
    }

    public void setAffectsTestResults(final Link[] affectsTestResults)
    {
        this.affectsTestResults.clear();

        if (affectsTestResults != null)
        {
            this.affectsTestResults.addAll(Arrays.asList(affectsTestResults));
        }
    }

    public void setApproved(final Boolean approved)
    {
        this.approved = approved;
    }

    public void setBlocksTestExecutionRecords(final Link[] blocksTestExecutionRecords)
    {
        this.blocksTestExecutionRecords.clear();

        if (blocksTestExecutionRecords != null)
        {
            this.blocksTestExecutionRecords.addAll(Arrays.asList(blocksTestExecutionRecords));
        }
    }

    public void setClosed(final Boolean closed)
    {
        this.closed = closed;
    }

    public void setCloseDate(final Date closeDate)
    {
        this.closeDate = closeDate;
    }

    public void setContributors(final List<Person> contributors)
    {
        this.contributors.clear();

        if (contributors != null)
        {
            this.contributors.addAll(contributors);
        }
    }

    public void setCreated(final Date created)
    {
        this.created = created;
    }

    public void setCreators(final List<Person> creators)
    {
        this.creators.clear();

        if (creators != null)
        {
            this.creators.addAll(creators);
        }
    }

    public void setDctermsTypes(final String[] dctermsTypes)
    {
        this.dctermsTypes.clear();

        if (dctermsTypes != null)
        {
            for (final String type : dctermsTypes)
            {
                this.dctermsTypes.add(Type.fromString(type));
            }
        }
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public void setDiscussedBy(final URI discussedBy)
    {
        this.discussedBy = discussedBy;
    }

    public void setFixed(final Boolean fixed)
    {
        this.fixed = fixed;
    }

    public void setIdentifier(final String identifier)
    {
        this.identifier = identifier;
    }

    public void setImplementsRequirements(final Link[] implementsRequirements)
    {
        this.implementsRequirements.clear();

        if (implementsRequirements != null)
        {
            this.implementsRequirements.addAll(Arrays.asList(implementsRequirements));
        }
    }

    public void setInProgress(final Boolean inProgress)
    {
        this.inProgress = inProgress;
    }

    public void setInstanceShape(final URI instanceShape)
    {
        this.instanceShape = instanceShape;
    }

    public void setModified(final Date modified)
    {
        this.modified = modified;
    }

    public void setRdfTypes(final URI[] rdfTypes)
    {
        this.rdfTypes.clear();

        if (rdfTypes != null)
        {
            this.rdfTypes.addAll(Arrays.asList(rdfTypes));
        }
    }

    public void setRelatedChangeRequests(final Link[] relatedChangeRequests)
    {
        this.relatedChangeRequests.clear();

        if (relatedChangeRequests != null)
        {
            this.relatedChangeRequests.addAll(Arrays.asList(relatedChangeRequests));
        }
    }

    public void setRelatedResources(final Link[] relatedResources)
    {
        this.relatedResources.clear();

        if (relatedResources != null)
        {
            this.relatedResources.addAll(Arrays.asList(relatedResources));
        }
    }

    public void setRelatedTestCases(final Link[] relatedTestCases)
    {
        this.relatedTestCases.clear();

        if (relatedTestCases != null)
        {
            this.relatedTestCases.addAll(Arrays.asList(relatedTestCases));
        }
    }

    public void setRelatedTestExecutionRecords(final Link[] relatedTestExecutionRecords)
    {
        this.relatedTestExecutionRecords.clear();

        if (relatedTestExecutionRecords != null)
        {
            this.relatedTestExecutionRecords.addAll(Arrays.asList(relatedTestExecutionRecords));
        }
    }

    public void setRelatedTestPlans(final Link[] relatedTestPlans)
    {
        this.relatedTestPlans.clear();

        if (relatedTestPlans != null)
        {
            this.relatedTestPlans.addAll(Arrays.asList(relatedTestPlans));
        }
    }

    public void setRelatedTestScripts(final Link[] relatedTestScripts)
    {
        this.relatedTestScripts.clear();

        if (relatedTestScripts != null)
        {
            this.relatedTestScripts.addAll(Arrays.asList(relatedTestScripts));
        }
    }

    public void setReviewed(final Boolean reviewed)
    {
        this.reviewed = reviewed;
    }

    public void setServiceProvider(final URI serviceProvider)
    {
        this.serviceProvider = serviceProvider;
    }

    public void setSeverity(final String severity)
    {
        this.severity = Severity.valueOf(severity);
    }

    public void setShortTitle(final String shortTitle)
    {
        this.shortTitle = shortTitle;
    }

    public void setStatus(final String status)
    {
        this.status = status;
    }

    public void setSubjects(final String[] subjects)
    {
        this.subjects.clear();

        if (subjects != null)
        {
            this.subjects.addAll(Arrays.asList(subjects));
        }
    }

    public void setTestedByTestCases(final Link[] testedByTestCases)
    {
        this.testedByTestCases.clear();

        if (testedByTestCases != null)
        {
            this.testedByTestCases.addAll(Arrays.asList(testedByTestCases));
        }
    }

    public void setTitle(final String title)
    {
        this.title = title;
    }

    public void setTracksChangeSets(final Link[] tracksChangeSets)
    {
        this.tracksChangeSets.clear();

        if (tracksChangeSets != null)
        {
            this.tracksChangeSets.addAll(Arrays.asList(tracksChangeSets));
        }
    }

    public void setTracksRequirements(final Link[] tracksRequirements)
    {
        this.tracksRequirements.clear();

        if (tracksRequirements != null)
        {
            this.tracksRequirements.addAll(Arrays.asList(tracksRequirements));
        }
    }

    public void setVerified(final Boolean verified)
    {
        this.verified = verified;
    }
}
